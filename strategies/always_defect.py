class AlwaysDefect:
    @staticmethod
    def author_name(): return "Pilat"
    @staticmethod
    def strategy_name(): return "Always Defect"
    def reset(self): pass
    def last_move(self, my, other): pass
    def play(self): return 'D'
def create_strategy(): return AlwaysDefect()