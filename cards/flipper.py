import PIL
from PIL import Image

sign = Image.open('sign.png')
for i in range(2, 14):
    base = Image.open(f'bg_card{i}.png')
    evened = i - i % 2
    center_x = 
    if evened == 2:
        
        pass
    else:
        pass

    flipped = im.transpose(Image.FLIP_LEFT_RIGHT).transpose(Image.FLIP_TOP_BOTTOM)
    cropped = im.crop((0, 0, im.width, im.height // 2))
    flipped.paste(cropped, (0,0))
    flipped.save(f'bg_card{i}.png')
    print('Done', i)