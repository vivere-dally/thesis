import re
from paddleocr import PaddleOCR
from dataclasses import dataclass
from typing import Union, List, Dict

from service.preprocessing import PreprocessingService


@dataclass(frozen=True, order=True)
class PredictedItem:
    value: Union[str, int, float]
    safe: bool


NA = PredictedItem('N/A', False)


class OCRService:

    def __init__(self, pp_srv: PreprocessingService):
        self.__pp_srv = pp_srv
        self.__paddle = PaddleOCR(use_angle_cls=True, lang='en')
        self.__prediction_threshold = 0.90

        self.__lei_re = r'^(lei*)$'
        factor_re = r'(?P<factor>[1-9]+\d*[,\.]?0{3})?'
        buc_re = r'(?P<buc>[bBuUlLiIcC]{0,4})?'
        times_re = r'(?P<times>[xX])?'
        price_re = r'(?P<price>[1-9]+\d*[,\.]?\d{2})?'
        self.__price_re = price_re + r'(?P<ignored>.*)'
        self.__product_quantity_re = fr"^({factor_re}\ ?{buc_re}\ ?{times_re}\ ?{price_re})$"
        self.__total_name_re = r'^(total)$'

    def __is_safe(self, accuracy: float) -> bool:
        return bool(self.__prediction_threshold < accuracy)

    def __extract_number(self, value: str, score: float, rex: str, rex_group_key: str,
                         normalize_factor: float) -> PredictedItem:
        m = re.search(rex, value)
        if not m or rex_group_key not in m.groupdict() or m.group(rex_group_key) is None:
            return NA

        group_match = m.group(rex_group_key)
        group = float(group_match.replace(',', '.')) if (',' in group_match) else float(group_match)
        if group_match.isdigit():
            group = group * normalize_factor

        return PredictedItem(group, self.__is_safe(score))

    def __extract_company(self, prediction: Union[None, List[list], list]) -> PredictedItem:
        value = prediction[0][1][0]
        score = prediction[0][1][1]
        return PredictedItem(value, self.__is_safe(score))

    def __extract_product_quantity(self, quantity: list) -> PredictedItem:
        return self.__extract_number(quantity[1][0], quantity[1][1], self.__product_quantity_re, 'factor', 1e-3)

    def __extract_product_price(self, price: list) -> PredictedItem:
        return self.__extract_number(price[1][0], price[1][1], self.__price_re, 'price', 1e-2)

    def __extract_product_name(self, name: list) -> PredictedItem:
        value = name[1][0]
        score = name[1][1]
        return PredictedItem(value, self.__is_safe(score))

    def __extract_product_price_total(self, price_total: list) -> PredictedItem:
        return self.__extract_number(price_total[1][0], price_total[1][1], self.__price_re, 'price', 1e-2)

    def __extract_products(self, prediction: Union[None, List[list], list]) -> List[Dict[str, PredictedItem]]:
        start_index = -1
        for index in range(len(prediction)):
            if re.search(self.__lei_re, prediction[index][1][0], flags=re.IGNORECASE) is not None:
                start_index = index + 1
                break

        if start_index == -1:
            return []

        products = []
        for index in range(start_index, len(prediction), 3):
            if index + 2 >= len(prediction):
                break

            quantity = self.__extract_product_quantity(prediction[index])
            price = self.__extract_product_price(prediction[index])
            name = self.__extract_product_name(prediction[index + 1])
            price_total = self.__extract_product_price_total(prediction[index + 2])

            if quantity is NA or price is NA or name is NA or price_total is NA:
                continue

            products.append({
                'quantity': quantity,
                'price': price,
                'name': name,
                'price_total': price_total
            })

        return products

    def __extract_total(self, prediction: Union[None, List[list], list]) -> PredictedItem:
        start_index = -1
        for index in range(len(prediction)):
            if re.search(self.__total_name_re, prediction[index][1][0], flags=re.IGNORECASE) is not None:
                start_index = index + 1
                break

        if start_index == -1:
            return NA

        value = prediction[start_index][1][0]
        score = prediction[start_index][1][1]
        return self.__extract_number(value, score, self.__price_re, 'price', 1e-2)

    def process(self, img: Union[str, bytes]) -> dict:
        __img = self.__pp_srv.process(img)
        prediction = self.__paddle.ocr(__img, cls=True)
        result = {
            'company': self.__extract_company(prediction),
            'products': self.__extract_products(prediction),
            'total': self.__extract_total(prediction)
        }

        return result
