import base64
from typing import Union

import cv2
import numpy as np
from PIL import Image
from io import BytesIO


def b64_img_encode(path: str) -> bytes:
    img = Image.open(path)
    buff = BytesIO()

    img.save(buff, format=img.format)
    return base64.urlsafe_b64encode(buff.getvalue())


def b64_img_decode(img: Union[str, bytes]) -> np.ndarray:
    buff = BytesIO(base64.urlsafe_b64decode(img))
    buff.flush()
    return np.array(Image.open(buff))


class PreprocessingService:

    def __init__(self):
        self.__erosion_kernel = np.ones((5, 5), np.uint8)
        self.__resize_scale = 0.3

    def __rm_background(self, img: np.ndarray) -> np.ndarray:
        imj = np.copy(img)
        imj = cv2.erode(imj, self.__erosion_kernel, iterations=3)
        _, imj_threshold = cv2.threshold(imj, 0, 255, cv2.THRESH_OTSU)
        x, y, w, h = cv2.boundingRect(imj_threshold)
        return img[y: y + h, x: x + w]

    def __resize(self, img: np.ndarray) -> np.ndarray:
        width = int(img.shape[1] * self.__resize_scale)
        height = int(img.shape[0] * self.__resize_scale)
        return cv2.resize(img, (width, height), interpolation=cv2.INTER_AREA)

    def process(self, img: Union[str, bytes]) -> np.ndarray:
        img = b64_img_decode(img)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
        img = self.__rm_background(img)
        img = cv2.equalizeHist(img)  # needs more testing
        img = self.__resize(img)

        return img
