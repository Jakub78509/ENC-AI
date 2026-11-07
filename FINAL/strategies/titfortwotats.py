class TitForTwoTats:
    @staticmethod
    def author_name(): return "Student"
    @staticmethod
    def strategy_name(): return "Tit For Two Tats"
    def __init__(self): self.reset()
    def reset(self): self.defects = 0
    def last_move(self, my, other):
        if other == 'D': self.defects += 1
        else: self.defects = 0
    def play(self):
        return 'D' if self.defects >= 2 else 'C'
def create_strategy(): return TitForTwoTats()