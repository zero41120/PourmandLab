import pyautogui as mouse
import pyautogui as keyboard
import pyautogui as gui
import time
import datetime 
import os
import re

def maximize_window():
	print('KEYBOARD: Win + Up')
	keyboard.hotkey('win', 'up')

def save_file():
	print('KEYBOARD: Ctrl + s')
	keyboard.hotkey('ctrl', 's')
	time.sleep(1)
	now = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
	current_path = os.path.dirname(os.path.realpath(__file__))
	output_path = current_path + os.sep + 'output' + os.sep
	file_name = 'temp_data_' + now + '.txt'
	file_path = output_path + file_name
	print('save file as', file_path)
	keyboard.typewrite(file_path)
	keyboard.typewrite(['tab', 'down','down','enter','enter'])
	return file_path

def click_lower_right_conor():
	print('MOUSE: Click lower right')
	x, y = gui.size()
	mouse.click(x, y)

def for_image_click_center(image_path, click = 1):
	try:
		print('MOUSE: Click center on ', image_path, ' for ', click)
		x, y = gui.locateCenterOnScreen(image_path)
		mouse.moveTo(x, y)
		while(click > 0):
			print('\tclicked')
			mouse.click()
			click -= 1
		time.sleep(1)
	except Exception as e:
		gui.alert("Failed: " + str(e))
		print(e)

def for_image_click_left_cornor(image_path, click = 1):
	try:
		print('MOUSE: Click left cornor on ', image_path, ' for ', click)
		if gui.locateOnScreen(image_path) != None:
			x, y, x2, y2 = gui.locateOnScreen(image_path)
			mouse.moveTo(x, y)
			while(click > 0):
				print('\tclicked')
				mouse.click()
				click -= 1
	except Exception as e:
		gui.alert("Failed: " + str(e))
		print(e)

def click_ok_button():
	for_image_click_center('media/ok_button.png')

def is_image_on_screen(image_path):
	image = gui.locateOnScreen(image_path)
	return image != None

def wait_until_image_is_found(image_path):
	image = gui.locateOnScreen(image_path)
	while(image == None):
		image = gui.locateOnScreen(image_path)	

def channel_array_disable(offset, channels):
	print(channels)
	for i in range(0, len(channels)):
		if(channels[i] == True):
			if(offset == 0):
				channels[i] = False;
			else:
				offset -= 1

	print(channels)
	return channels


## Ask user condition
upper_bound = 0.0
while(True):
	try:
		upper_bound = float(input('Please enter the upper bound in nA (i.e. 2 = 2 nA = 2x10^-9)'))*10**-9
		break
	except Exception as e:
		print("You didn't enter a valid float number")

## Open the CHI program window
gui.alert('Please start the CHI program and set 8 channel active, then click OK')
click_lower_right_conor()
for_image_click_center('media/chi_icon.png')
time.sleep(1)
maximize_window()

## Collect infomation on active channels
for_image_click_center('media/parameter_button.png')
active_channels = [False, False, False, False, False, False, False, False]
if(is_image_on_screen('media/on_all.png')):
	active_channels = [True, True, True, True, True, True, True, True]
else:
	for i in range(1, 9):
		image_path = 'media/on_e' + str(i) + '.png'
		if(is_image_on_screen(image_path)):
			active_channels[i-1] = True
print(active_channels)
keyboard.press('esc')

## Run cycles until all channels are disabled 
while(True):
	## Run a cycle and save file
	mouse.moveTo(20, 20) # so start is not highlighted
	for_image_click_center('media/start_button.png')
	wait_until_image_is_found('media/start_button.png') # when running, button is a disabled icon
	file_name = save_file()
	time.sleep(1)

	## Open saved file and check channels
	file = open(file_name, 'r')
	import sys
	for line in file:
		words = re.split('\t', line)
		words.pop(0) # First column not used
		for i in range(0, len(words)):
			if(float(words[i]) > float(upper_bound)):
				print(active_channels)
				active_channels = channel_array_disable(i, active_channels)
	print("File is read")
	time.sleep(2)

	## Disable channels
	try:
		for_image_click_center('media/parameter_button.png')
		for i in range(0, len(active_channels)-1):
			print(active_channels,"we are here",i)
			if(active_channels[i] == False):
				image_path = 'media/on_e' + str(i+1) + '.png'
				for_image_click_left_cornor(image_path)
		click_ok_button()
	except Exception as e:
		gui.alert("Failed: " + str(e))
		print(e)

	## End loop when all channel are disabled
	if(all(channel == False for channel in active_channels)):
		break
	time.sleep(2)  