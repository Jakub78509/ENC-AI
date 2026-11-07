from strategies.q_learning import QLearningAgent

class FastQLearningAgent(QLearningAgent):
    @staticmethod
    def strategy_name(): return "Q-Learning (alpha=0.5, eps=0.02)"

def create_strategy(): return FastQLearningAgent(alpha=0.5, epsilon=0.02)