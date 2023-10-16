# Computes both players distributions for 2-person, 0-sum Nash equilibrium
# p. 286 of G. B. Dantzig, Linear Programming and Extensions

# Combines 2pers0sum.max.mod and 2pers0sum.min.mod to eliminate the need for
# a min/maximize objective.

# glpsol --model 2pers0sum.mod --data rps.dat

param m, integer, > 0;

param n, integer, > 0;

set I := 1..m; # rows

set J := 1..n; # columns

param a{i in I, j in J}; # input matrix, a[i,j] is payoff for i, -a[i,j] is payoff to j

var x{i in I}, >= 0;

var y{i in J}, >= 0;

var V;

s.t. xsum{i in J}: sum{j in I} a[j,i]*x[j] >= V;

s.t. xprob: sum{i in I} x[i] = 1;

s.t. ysum{i in I}: sum{j in J} a[i,j]*y[j] <= V;

s.t. yprob: sum{i in J} y[i] = 1;

solve;

printf "\n";
printf "V is %10g\n",V;
printf "X distribution is:";
printf{i in I} " (%d %10g)",i,x[i];
printf "\n";
printf "Y distribution is:";
printf{i in J} " (%d %10g)",i,y[i];
printf "\n";

end;
