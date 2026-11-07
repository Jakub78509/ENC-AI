import random
class RandomStrat:
    @staticmethod
    def author_name(): return "Pilat"
    @staticmethod
    def strategy_name(): return "Random"
    def reset(self): pass
    def last_move(self, my, other): pass
    def play(self): return 'C' if random.randint(0,1) == 0 else 'D'
def create_strategy(): return RandomStrat()