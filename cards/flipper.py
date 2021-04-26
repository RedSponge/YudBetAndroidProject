#!/usr/bin/python3

import PIL
from PIL import Image

def draw_sign(base, sign, pos):
    center_y = base.height // 2

    if pos[1] > center_y:
        sign = sign.transpose(Image.FLIP_TOP_BOTTOM)

    base.paste(sign, (pos[0] - sign.width // 2, pos[1] - sign.height // 2), sign)

def main():
    names = ['spade', 'club', 'heart', 'diamond']
    for n in range(1, 5):
        sign = Image.open(f'sign_{n}.png')
        for i in range(2, 11):
            base = Image.open(f'bg_{"red_" if n > 2 else ""}card{i}.png')
            evened = i - i % 2
            height = base.height
            width = base.width
            center_x = width // 2
            if evened == 2:
                draw_sign(base, sign, (center_x, int(height * 0.2)))
                draw_sign(base, sign, (center_x, int(height * 0.8)))
            else:
                rows = evened // 2
                rows_evened = rows - rows % 2
                percents = []

                sizes = [0.5, 0.5, 0.5, 0.6, 0.75]
                for j in range(0, rows_evened // 2):
                    print('step for', i, 'is ', (0.5 / rows_evened))
                    percents.append(0.5 - (j + 1) * (sizes[rows - 1] / rows))
                    percents.append(0.5 + (j + 1) * (sizes[rows - 1] / rows))
                if rows % 2 == 1:
                    percents.append(0.5)

                margin = 0
                drawn_height = height - margin * 2
                for percent in percents:
                    draw_sign(base, sign, (int(width * 0.3), int(drawn_height * percent) + margin))
                    draw_sign(base, sign, (int(width * 0.7), int(drawn_height * percent) + margin))
                
                pass

            if i % 2 == 1:
                draw_sign(base, sign, (center_x, int(height // 2)))

            base.save(f'output/{names[n - 1]}{i}.png')

    #        flipped = im.transpose(Image.FLIP_LEFT_RIGHT).transpose(Image.FLIP_TOP_BOTTOM)
    #        cropped = im.crop((0, 0, im.width, im.height // 2))
    #        flipped.paste(cropped, (0,0))
    #        flipped.save(f'bg_card{i}.png')
            print('Done', i)

if __name__ == "__main__":
    main()
