// Finding maximum matching via ascending auction
// Section 17.4 of Karlin and Peres, "Game Theory, Alive"

// Lowest and highest envy-free price vectors and utility vectors, section 17.2
// Theorem 17.2.6

// Also does envy-free rent division, section 17.3

// Coded with rationals to avoid floating point issues

#include <stdio.h>
#include <stdlib.h>

#define MAXN (100)

#define UNMATCHED (-1)

void error(char *str,int num) {
  printf("%s %d\n",str,num);
  exit(num);
  }

void readInput(int *n,int v[MAXN][MAXN]) {
  scanf("%d",n);
  if (*n<1 || *n>MAXN)
    error("range",__LINE__);

  for (int i=0; i<*n; i++)
    for (int j=0; j<*n; j++) {
      scanf("%d",&v[i][j]);
      if (v[i][j]<0)
        error("range",__LINE__);
      }
  }

int ascendingAuction(int n,int v[MAXN][MAXN],int m[MAXN]) {
  int deltaNum=1;  // Numerator for delta, denominator is n+1
  int matchCard;   // Cardinality of matching
  int sum=0;

  int vNum[MAXN][MAXN];  // numerator for rational form of v, denominator is n+1

  int mInverse[MAXN];  // inverse of m[]

  int pNum[MAXN];  // Numerator for price, denominator is n+1

  // Convert v to rational form
  for (int i=0; i<n; i++)
    for (int j=0; j<n; j++)
      vNum[i][j]=v[i][j]*(n+1);

  for (int i=0; i<n; i++) {
    m[i]=UNMATCHED;
    mInverse[i]=UNMATCHED;
    pNum[i]=0;  // Item i price starts at 0
    }
  matchCard=0;  // Not in KP, cardinality makes termination easy

  while (matchCard<n) {
    // Find unmatched bidder i and item j in his demand set
    int i,j,k;

    for (i=0; ; i++)
      if (m[i]==UNMATCHED) {
        for (j=0; ; j++)
          if (vNum[i][j]>=pNum[j]) {
            // Test the candidate match
            for (k=0; k<n; k++)
              if (vNum[i][j]-pNum[j] < vNum[i][k]-pNum[k])
                break;  // item j is not in i's demand set

            if (k==n)
              break;  // item j survived
            }
        break;  // Must have item j for bidder i       
        }

    if (mInverse[j]==UNMATCHED) {
      m[i]=j;
      mInverse[j]=i;
      matchCard++;
      }
    else {
      m[mInverse[j]]=UNMATCHED;

      m[i]=j;
      mInverse[j]=i;
      }

    pNum[j]++;
    }

  for (int i=0; i<n ; i++)
    sum+=v[i][m[i]];
  return sum;
  }

void printMatching(int n,int m[MAXN],int v[MAXN][MAXN]) {
  printf("Matching:\n");
  for (int i=0; i<n ; i++)
    printf("%d %d\n",i,m[i]);
  }

int main() {
  int n;

  int v[MAXN][MAXN];

  int m[MAXN];  // permutation for matching

  int pLowest[MAXN],pHighest[MAXN];

  int uLowest[MAXN],uHighest[MAXN];

  int vTemp[MAXN][MAXN];

  int mTemp[MAXN];

  int sum;

  int sumLowest,sumHighest;

  readInput(&n,v);

  sum=ascendingAuction(n,v,m);

  printf("Total weight %d\n",sum);

  printMatching(n,m,v);

  // Lowest envy-free prices
  for (int i=0; i<n; i++) {
    // Copy from original v
    for (int j=0; j<n; j++)
      for (int k=0; k<n; k++)
        vTemp[j][k]=v[j][k];

    // Zero row i
    for (int j=0; j<n; j++)
      vTemp[i][j]=0;

    int sumTemp=ascendingAuction(n,vTemp,mTemp);
    // Note:  m[i] is called j in Karlin & Peres
    pLowest[m[i]]=sumTemp-(sum-v[i][m[i]]);  // p. 302 (17.5)

    uLowest[i]=sum-sumTemp;  // p. 302 (17.6)
    }

  printf("p lowest:\n");
  sumLowest=0;
  for (int i=0; i<n; i++) {
    printf("%d %d\n",i,pLowest[i]);
    sumLowest+=pLowest[i];
    }
  printf("Lowest envy-free price sum:  %d\n",sumLowest);

  printf("u lowest:\n");
  for (int i=0; i<n; i++)
    printf("%d %d\n",i,uLowest[i]);

  // Highest envy-free prices
  for (int i=0; i<n; i++) {
    // Copy from original v
    for (int j=0; j<n; j++)
      for (int k=0; k<n; k++)
        vTemp[j][k]=v[j][k];

    // Note:  m[i] is called j in Karlin & Peres
    // Zero column j
    for (int k=0; k<n; k++)
      vTemp[k][m[i]]=0;

    int sumTemp=ascendingAuction(n,vTemp,mTemp);

    uHighest[i]=sumTemp-(sum-v[i][m[i]]);  // p. 304 (17.9)

    pHighest[m[i]]=sum-sumTemp;  // p. 304 (17.10)
    }

  printf("p highest:\n");
  sumHighest=0;
  for (int i=0; i<n; i++) {
    printf("%d %d\n",i,pHighest[i]);
    sumHighest+=pHighest[i];
    }
  printf("Highest envy-free price sum:  %d\n",sumHighest);

  printf("u highest:\n");
  for (int i=0; i<n; i++)
    printf("%d %d\n",i,uHighest[i]);

  double desiredRentTotal;
  scanf("%lf",&desiredRentTotal);
  while (desiredRentTotal!=0) {
    if (desiredRentTotal<sumLowest || desiredRentTotal>sumHighest)
      printf("Desired rent is outside of fairness range\n");
    else {
      // See KP p. 305
      double alpha=(sumHighest-desiredRentTotal)/(sumHighest-sumLowest);

      printf("Envy-free prices using alpha %f:\n",alpha);
      for (int i=0; i<n; i++)
        printf("%d %f\n",i,alpha*pLowest[i]+(1.0-alpha)*pHighest[i]);
      }

    scanf("%lf",&desiredRentTotal);
    }
  }