// uklízecí robot (původně r1)
// Místo pevného jména r1 používá .my_name(Me), takže může běžet ve více instancích.

/* Pravidla */

// stojím na stejném místě jako agent (nebo zapamatované místo) P
at(P) :- .my_name(Me) & pos(P,X,Y) & pos(Me,X,Y).

// počet kroků mezi dvěma políčky; move_towards hýbe v x i y zároveň
// (umí diagonálu), takže skutečná vzdálenost je Čebyševova
dist(X1,Y1,X2,Y2,D) :- D = math.max(math.abs(X1-X2), math.abs(Y1-Y2)).

// ÚKOL 2: nejbližší spalovač k mé aktuální pozici
nearest_burner(B) :-
    .my_name(Me) & pos(Me,X,Y) &
    .findall(d(D,Bn), burner(Bn) & pos(Bn,BX,BY) & dist(X,Y,BX,BY,D), L) &
    .min(L, d(_,B)).

// stojím na políčku spalovače (odpadek tam patří jemu, ne mně)
on_burner :- .my_name(Me) & pos(Me,X,Y) & burner(B) & pos(B,X,Y).

/* Počáteční cíl */

!check(slots).

/* Plány */

// procházení mřížky
+!check(slots) : .my_name(Me) & (not garbage(Me) | on_burner)
   <- next(slot);
      !check(slots).
+!check(slots).   // na políčku je odpadek -> převezme to plán +garbage

// našel jsem odpadek
+garbage(Me) : .my_name(Me) & not on_burner & not .desire(carry_garbage)
   <- !carry_garbage.

+!carry_garbage
   <- .drop_desire(check(slots));   // přestaň prohledávat

      // zapamatuj si, kam se vrátit
      .my_name(Me);
      ?pos(Me,X,Y);
      -+pos(last,X,Y);

      // seber odpadek a odnes ho k nejbližšímu spalovači
      !ensure_pick(garb);
      ?nearest_burner(B);
      .print("Nesu odpadek z (", X, ",", Y, ") ke spalovači ", B);
      !at(B);
      drop(garb);

      // vrať se a pokračuj v prohledávání
      !at(last);
      !!check(slots).

+!ensure_pick(S) : .my_name(Me) & garbage(Me)
   <- pick(garb);
      !ensure_pick(S).
+!ensure_pick(_).

+!at(L) : at(L).
+!at(L) <- ?pos(L,X,Y);
           move_towards(X,Y);
           !at(L).
