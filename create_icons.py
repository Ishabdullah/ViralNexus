#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

def create_icon(size, path):
    # Create a simple icon with viral theme
    img = Image.new('RGBA', (size, size), (26, 26, 46, 255))  # Dark background
    draw = ImageDraw.Draw(img)
    
    # Draw a simple virus-like icon
    center = size // 2
    radius = size // 3
    
    # Main virus body (green)
    draw.ellipse([center-radius, center-radius, center+radius, center+radius], 
                fill=(0, 255, 136, 255), outline=(0, 200, 100, 255), width=2)
    
    # Infection dots (red)
    dot_size = size // 10
    for i in range(6):
        angle = i * 60 * 3.14159 / 180
        x = center + int((radius + dot_size) * cos(angle))
        y = center + int((radius + dot_size) * sin(angle))
        draw.ellipse([x-dot_size//2, y-dot_size//2, x+dot_size//2, y+dot_size//2], 
                    fill=(255, 68, 68, 255))
    
    img.save(path)

# Icon sizes for different densities
icon_sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

import math
cos = math.cos
sin = math.sin

base_path = '/Users/ismailabdullah/ViralNexus/app/src/main/res/'

for folder, size in icon_sizes.items():
    folder_path = os.path.join(base_path, folder)
    os.makedirs(folder_path, exist_ok=True)
    
    create_icon(size, os.path.join(folder_path, 'ic_launcher.png'))
    create_icon(size, os.path.join(folder_path, 'ic_launcher_round.png'))

print("Icons created successfully!")