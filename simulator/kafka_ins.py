import torch
import cv2
import matplotlib.pyplot as plt
import time
import argparse
from typing import List, Optional, Union
import numpy as np
import norfair
from norfair import Detection, Paths, Tracker, Video
from collections import deque
from op_color import *
from confluent_kafka import Producer

bootstrap_servers = 'localhost:9092'
topic = 'events'

conf = {
	'bootstrap.servers': bootstrap_servers,
}

def delivery_report(err, msg):
	if err is not None:
		print('Message delivery failed:', err)
	else:
		print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))
		
producer = Producer(conf)

# thresholds for object tracing
DISTANCE_THRESHOLD_BBOX: float = 0.7
DISTANCE_THRESHOLD_CENTROID: int = 30
MAX_DISTANCE: int = 10000
HISTOGRAM_THRESHOLD = 1


## YOLO CLASS
class YOLO:
	def __init__(self, model_name: str, device: Optional[str] = None):
		if device is not None and "cuda" in device and not torch.cuda.is_available():
			raise Exception(
				"Selected device='cuda', but cuda is not available to Pytorch."
			)
		elif device is None:
			device = "cuda:0" if torch.cuda.is_available() else "cpu"
		self.model = torch.hub.load("ultralytics/yolov5", model_name, device=device)
	def __call__(
		self,
		img: Union[str, np.ndarray],
		conf_threshold: float = 0.25,
		iou_threshold: float = 0.45,
		image_height: int = 640,
		image_width: int = 640,
		classes: Optional[List[int]] = None,
	) -> torch.tensor:
		self.model.conf = conf_threshold
		self.model.iou = iou_threshold
		if classes is not None:
			self.model.classes = classes
		detections = self.model(img, size=(image_height,image_width))
		return detections

# functions for tarcking objects in video
def center(points):
	return [np.mean(np.array(points), axis=0)]
def yolo_detections_to_norfair_detections(
	yolo_detections: torch.tensor, track_points: str = "centroid"  # bbox or centroid
) -> List[Detection]:
	"""convert detections_as_xywh to norfair detections"""
	norfair_detections: List[Detection] = []
	if track_points == "centroid":
		detections_as_xywh = yolo_detections.xywh[0]
		for detection_as_xywh in detections_as_xywh:
			centroid = np.array(
				[detection_as_xywh[0].item(), detection_as_xywh[1].item()]
			)
			scores = np.array([detection_as_xywh[4].item()])
			norfair_detections.append(
				Detection(
					points=centroid,
					scores=scores,
					label=int(detection_as_xywh[-1].item()),
				)
			)
	elif track_points == "bbox":
		detections_as_xyxy = yolo_detections.xyxy[0]
		for detection_as_xyxy in detections_as_xyxy:
			bbox = np.array(
				[
					[detection_as_xyxy[0].item(), detection_as_xyxy[1].item()],
					[detection_as_xyxy[2].item(), detection_as_xyxy[3].item()],
				]
			)
			scores = np.array(
				[detection_as_xyxy[4].item(), detection_as_xyxy[4].item()]
			)
			norfair_detections.append(
				Detection(
					points=bbox, scores=scores, label=int(detection_as_xyxy[-1].item())
				)
			)
	return norfair_detections

# Function to answer query for finding truck in a given video
def insert_data(start,end,chunk_size,step_size,argum) : 

	# detecting the model to be used
	model = YOLO(args.model_name, device=args.device)

	for input_video_path in argum.files:

		# Initialize tracker
		distance_function = "iou" if argum.track_points == "bbox" else "euclidean"
		distance_threshold = (
			DISTANCE_THRESHOLD_BBOX
			if argum.track_points == "bbox"
			else DISTANCE_THRESHOLD_CENTROID
		)
		tracker = Tracker(
			distance_function=distance_function,
			distance_threshold=distance_threshold,
		)

		# Open the Video Capture
		cap = cv2.VideoCapture(input_video_path)
		fps = int(cap.get(5))
		if not cap.isOpened():
			print("Error opening video file.")
			return
		
		# Get the video frame width and height
		frame_width = int(cap.get(3))
		frame_height = int(cap.get(4))
		
		# Define the codec and create VideoWriter object
		output_video_path = 'output_video.mp4'  # Change to your output video path
		fourcc = cv2.VideoWriter_fourcc(*'XVID')
		out = cv2.VideoWriter(output_video_path, fourcc, fps, (frame_width, frame_height))

		
		# Indices range to be processed based on starting and ending time passed in function
		start_frame = fps*start
		end_frame = fps*end

		frame_count = 0

		# Process video from start frame
		cap.set(cv2.CAP_PROP_POS_FRAMES, start_frame)
		prev_hist = None
		while cap.isOpened() and start_frame < end_frame:

			# Read next frame
			start_frame+=1
			frame_count += 1
			print(start_frame)
			ret, frame = cap.read()
			if not ret:
				break

			# Calculate the histogram of the current frame and Normalize it
			hist = cv2.calcHist([frame], [0], None, [256], [0, 256])
			hist = hist / hist.sum()

			# Process current frame
			if prev_hist is None or cv2.compareHist(hist, prev_hist, cv2.HISTCMP_INTERSECT) < HISTOGRAM_THRESHOLD:
				prev_hist = hist

				# Perform object detection on the frame
				results = model(
					frame,
					conf_threshold=argum.conf_threshold,
					iou_threshold=argum.iou_threshold,
					image_height=argum.img_height,
					image_width=argum.img_width,
					classes=argum.classes,
				)

				rendered_frame = results.render()[0]

				# Convert the rendered frame to BGR format
				rendered_frame = cv2.cvtColor(rendered_frame, cv2.COLOR_RGB2BGR)

				# Write the frame with bounding boxes to the output video
				out.write(rendered_frame)

				# Display the frame
				cv2.imshow('Video Analytics',rendered_frame)

				# Exit when 'q' key is pressed
				if cv2.waitKey(1) & 0xFF == ord('q'):
					break

				# Do object tracking
				detections = yolo_detections_to_norfair_detections(
					results, track_points=argum.track_points
				)
				tracked_objects = tracker.update(detections=detections)

				for obj in tracked_objects:
					obj_box = obj.past_detections[-1].points
					color_obj = color(frame,obj)
					obj_data = (start_frame,obj.id,obj.label,color_obj,obj_box[0][0],obj_box[0][1],obj_box[1][0],obj_box[1][1])
					key = "key"
					str_obj_data = str(obj_data)
					print(str_obj_data)
					producer.produce(topic, key=key, value=str_obj_data, on_delivery=delivery_report)

				producer.flush()
			

				# insert the data into snowflake       
			else:
				print("The frame is not taken !! ",start_frame)

			# If complete window is processed store results and reset for next window
			if (frame_count == chunk_size*fps) :
				frame_count=0
				tracker = Tracker(
					distance_function=distance_function,
					distance_threshold=distance_threshold,
				)
		cap.release()
		out.release()
		cv2.destroyAllWindows()

##parser arguements
parser = argparse.ArgumentParser(description="Track objects in a video.")
parser.add_argument("files", type=str, nargs="+", help="Video files to process")
parser.add_argument("--model-name", type=str, default="yolov5m", help="YOLOv5 model name")
parser.add_argument("--img-height", type=int, default="640", help="YOLOv5 inference height (pixels)")
parser.add_argument("--img-width", type=int, default="640", help="YOLOv5 inference width (pixels)")
parser.add_argument("--conf-threshold",type=float,default="0.25",help="YOLOv5 object confidence threshold",)
parser.add_argument("--iou-threshold", type=float, default="0.45", help="YOLOv5 IOU threshold for NMS")
parser.add_argument("--classes",nargs="+",type=int,help="Filter by class: --classes 0, or --classes 0 2 3",)
parser.add_argument("--device", type=str, default=None, help="Inference device: 'cpu' or 'cuda'")
parser.add_argument("--track-points",type=str,default="centroid",help="Track points: 'centroid' or 'bbox'",)
args = parser.parse_args()

# start time of program
start_time = time.time()
print(insert_data(0,25,5,5,args))
end_time = time.time()
elapsed_time = end_time - start_time
print("Elapsed time:", elapsed_time)

producer.flush()