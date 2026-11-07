class AlwaysCooperate:
    @staticmethod
    def author_name(): return "Pilat"
    @staticmethod
    def strategy_name(): return "Always Cooperate"
    def reset(self): pass
    def last_move(self, my, other): pass
    def play(self): return 'C'
def create_strategy(): return AlwaysCooperate()