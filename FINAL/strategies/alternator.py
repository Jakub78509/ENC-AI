class Alternator:
    @staticmethod
    def author_name(): return "Student"
    @staticmethod
    def strategy_name(): return "Alternator (C, D, C, D, ...)"
    def __init__(self): self.reset()
    def reset(self): self.next = 'C'
    def last_move(self, my, other): self.next = 'D' if my == 'C' else 'C'
    def play(self): return self.next
def create_strategy(): return Alternator()