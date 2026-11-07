import random
import numpy as np

class QLearningAgent:
    @staticmethod
    def author_name(): return "Student AI"
    @staticmethod
    def strategy_name(): return "Q-Learning Agent"

    def __init__(self, alpha=0.1, gamma=0.9, epsilon=0.1):
        self.alpha = alpha     # Rychlost učení
        self.gamma = gamma     # Diskontní faktor
        self.epsilon = epsilon # Míra průzkumu (exploration)
        # Stavy: 'START', 'C', 'D' (předchozí tah soupeře)
        # Akce: 0 = 'C', 1 = 'D'
        # Q-tabulka vzniká jen jednou, aby si agent naučené hodnoty přenášel mezi zápasy a generacemi
        self.q_table = {
            'START': [3.0, 3.0],
            'C': [3.0, 3.0],
            'D': [1.0, 1.0]
        }
        # Výchozí odměny Vězňova dilematu, simulátor je přepíše přes set_reward_matrix()
        self.reward_map = {('C','C'):3, ('C','D'):0, ('D','C'):5, ('D','D'):1}
        self.reset()

    def set_reward_matrix(self, matrix):
        # Odměny se berou z matice, se kterou běží simulace (např. Stag Hunt)
        self.reward_map = {moves: rewards[0] for moves, rewards in matrix.items()}

    def reset(self):
        # Na začátku zápasu se resetuje jen stav, ne naučené Q-hodnoty
        self.current_state = 'START'
        self.last_action_idx = None

    def last_move(self, my_move, other_move):
        r = self.reward_map.get((my_move, other_move), 0)

        next_state = other_move

        # Q-Learning update
        if self.last_action_idx is not None:
            old_q = self.q_table[self.current_state][self.last_action_idx]
            max_future_q = max(self.q_table[next_state])
            new_q = old_q + self.alpha * (r + self.gamma * max_future_q - old_q)
            self.q_table[self.current_state][self.last_action_idx] = new_q

        self.current_state = next_state

    def play(self):
        # Epsilon-greedy výběr akce
        if random.random() < self.epsilon:
            action_idx = random.choice([0, 1])
        else:
            q_vals = self.q_table[self.current_state]
            action_idx = int(np.argmax(q_vals))

        self.last_action_idx = action_idx
        return 'C' if action_idx == 0 else 'D'

def create_strategy(): return QLearningAgent()