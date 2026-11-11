import random

class TitForTwoTats:
    @staticmethod
    def author_name():
        return "Jakub Procházka"

    @staticmethod
    def strategy_name():
        return "Tit For Two Tats"

    def __init__(self):
        self.reset()

    def reset(self):
        # TODO: Inicializujte paměť na předchozí zrady soupeře
        self.consecutive_defects = 0

    def last_move(self, my_move, other_move):
        # TODO: Aktualizujte čítač zrad na základě other_move
        if other_move == 'D':
            self.consecutive_defects += 1
        else:
            self.consecutive_defects = 0

    def play(self):
        # TODO: Vraťte 'D' pouze pokud soupeř zradil 2x po sobě, jinak 'C'
        if self.consecutive_defects >= 2:
            return 'D'
        return 'C'

def create_strategy():
    return TitForTwoTats()
