import random

class AdaptiveDetective:
    # Adaptivní detektiv:
    #   1) Sonda: první tahy C, D, C, C - zjistí, zda soupeř trestá zradu.
    #   2) Soupeř nikdy neoplatil (Always Cooperate, TF2T) -> VYUŽITÍ:
    #      střídá D/C (nikdy dvě zrady po sobě, takže TF2T neoplatí).
    #      Jakmile soupeř zradí, přepne na reciprocitu.
    #   3) Soupeř oplácí (Tit for Tat) -> RECIPROCITA: velkorysé Tit-for-Tat
    #      (po vzájemné zradě občas nabídne smír, aby nevznikla spirála odvety).
    #   4) Soupeř často zrazuje bez provokace (Random, Always Defect)
    #      -> TREST: zrazuje pořád.
    PROBE = ['C', 'D', 'C', 'C']
    MIN_ROUNDS = 6           # minimální počet kol pro odhad chování soupeře
    UNPROVOKED_LIMIT = 0.3   # podíl nevyprovokovaných zrad, od kterého trestáme
    FORGIVENESS = 0.1        # pravděpodobnost smíru po vzájemné zradě

    @staticmethod
    def author_name():
        return "Jakub Procházka"

    @staticmethod
    def strategy_name():
        return "Adaptive Detective"

    def __init__(self):
        self.reset()

    def reset(self):
        self.history_other = []
        self.history_my = []
        self.mode = 'probe'

    def last_move(self, my_move, other_move):
        self.history_my.append(my_move)
        self.history_other.append(other_move)

    def _unprovoked_rate(self):
        # Podíl zrad soupeře v kolech, kdy jsem v předchozím kole spolupracoval
        chances, defects = 0, 0
        for t in range(1, len(self.history_other)):
            if self.history_my[t - 1] == 'C':
                chances += 1
                if self.history_other[t] == 'D':
                    defects += 1
        return defects / chances if chances else 0.0

    def play(self):
        n = len(self.history_other)

        # 1) Sonda
        if n < len(self.PROBE):
            return self.PROBE[n]
        if self.mode == 'probe':
            self.mode = 'exploit' if 'D' not in self.history_other else 'reciprocate'

        # 4) Trest za nevyprovokované zrady
        if n >= self.MIN_ROUNDS and self._unprovoked_rate() >= self.UNPROVOKED_LIMIT:
            return 'D'

        # 2) Využití soupeře, který neoplácí
        if self.mode == 'exploit':
            if self.history_other[-1] == 'D':
                self.mode = 'reciprocate'
                return 'C'  # omluva a přechod na reciprocitu
            return 'C' if self.history_my[-1] == 'D' else 'D'

        # 3) Velkorysé Tit-for-Tat
        if self.history_other[-1] == 'D':
            if self.history_my[-1] == 'D' and random.random() < self.FORGIVENESS:
                return 'C'
            return 'D'
        return 'C'

def create_strategy():
    return AdaptiveDetective()
