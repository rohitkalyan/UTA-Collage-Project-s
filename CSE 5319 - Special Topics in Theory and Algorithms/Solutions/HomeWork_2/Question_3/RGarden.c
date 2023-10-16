

#include <stdio.h>

int main() {
  int binChoice[6];
  int i,sum=0;
  int ballCount[6];

  // Generate each mapping for 6 agents to 6 edges
  for (binChoice[0]=0; binChoice[0]<6; binChoice[0]++)
    for (binChoice[1]=0; binChoice[1]<6; binChoice[1]++)
      for (binChoice[2]=0; binChoice[2]<6; binChoice[2]++)
        for (binChoice[3]=0; binChoice[3]<6; binChoice[3]++)
          for (binChoice[4]=0; binChoice[4]<6; binChoice[4]++)
            for (binChoice[5]=0; binChoice[5]<6; binChoice[5]++) {
          // Clear the edges
          for (i=0;i<6;i++)
            ballCount[i]=0;
          // Count agents for each edge
          for (i=0;i<6;i++)
            ballCount[binChoice[i]]++;
          // Accumulate c(x)=x costs
          for (i=0;i<6;i++)
            sum+=ballCount[i]*ballCount[i];
          }
  // 6 agents * number of choices for choosing bin simultaneously
  printf("Expected cost %10.6f\n",((double) sum)/(6*6*6*6*6*6*6));
  }