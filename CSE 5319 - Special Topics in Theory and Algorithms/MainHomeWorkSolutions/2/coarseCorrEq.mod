# Coarse correlated equilibrium, see moulinVial.cce.pdf "Strategically Zero-Sum Games", p. 204.
# and Karlin/Peres p. 143-144

# glpsol --model coarseCorrEq.mod --data battleSexes.dat

param m, integer, > 0;

param n, integer, > 0;

set I := 1..m; # rows

set J := 1..n; # columns

param a{i in I, j in J}; # input matrix, a[i,j] is payoff for row player

param b{i in I, j in J}; # input matrix, b[i,j] is payoff for column player

var z{i in I, j in J}, >= 0; # KP Figure 7.4

var asum;

var bsum;

var objective;

s.t. zprob: sum{i in I, j in J} z[i,j] = 1;

s.t. aconstraint{iPrime in I}: sum{i in I, j in J} z[i,j]*(a[i,j] - a[iPrime,j]) >= 0; # MV, p. 204

s.t. bconstraint{jPrime in J}: sum{i in I, j in J} z[i,j]*(b[i,j] - b[i,jPrime]) >= 0; # MV, p. 204

# From Multiagent Systems, Shoham and Leyton-Brown, p. 114, expression (4.55)

s.t. computeasum: asum = sum{i in I, j in J} z[i,j]*a[i,j];

s.t. computebsum: bsum = sum{i in I, j in J} z[i,j]*b[i,j];

s.t. oconstraint: asum + bsum = objective;

#s.t. payoff: 1.0 = objective; # Force a particular value

#s.t. equalize: sum{i in I, j in J} z[i,j]*a[i,j] = sum{i in I, j in J} z[i,j]*b[i,j]; # Identical payoffs

maximize obj: objective;

solve;

printf "\n";
printf "a payoff %10g b payoff %10g objective is %10g\n",asum,bsum,objective;
printf "z distribution is:\n";
printf{i in I, j in J} " (%d %d %10g)\n",i,j,z[i,j];

end;
