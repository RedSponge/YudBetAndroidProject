#!/usr/bin/python3

import PIL
from PIL import Image
import os

image_files = [f for f in os.listdir('.') if f.endswith('.png')]
image_files.sort()
card_size = (64, 96)
cards_per_row = 13   # amount in suit
card_rows = 5  # Utils (back/jokers) and one per suit

output_image_size = (card_size[0] * cards_per_row, card_size[1] * card_rows)

output_image = Image.new(mode='RGBA', size=output_image_size)

back = Image.open('back.png')
output_image.paste(back, (0,0))

suits = ['spade', 'club', 'diamond', 'heart']
types = list(range(1, 10)) + ['T', 'J', 'Q', 'K']

for i in range(1, card_rows):
    suit = suits[i - 1]
    for j in range(cards_per_row):
        name = suit + str(types[j])
        try:
            with Image.open(f'{name}.png') as im:
                output_image.paste(im, (card_size[0] * j, card_size[1] * i))
        except:
            print("Skipping", name)
        


output_image.save('packed.png')
print('Done!')


