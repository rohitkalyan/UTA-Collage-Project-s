/* gmicut.c (Gomory's mixed integer cut generator) */

/***********************************************************************
*  This code is part of GLPK (GNU Linear Programming Kit).
*
*  Copyright (C) 2002-2016 Andrew Makhorin, Department for Applied
*  Informatics, Moscow Aviation Institute, Moscow, Russia. All rights
*  reserved. E-mail: <mao@gnu.org>.
*
*  GLPK is free software: you can redistribute it and/or modify it
*  under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  GLPK is distributed in the hope that it will be useful, but WITHOUT
*  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
*  License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with GLPK. If not, see <http://www.gnu.org/licenses/>.
***********************************************************************/

#include "env.h"
#include "prob.h"

/***********************************************************************
*  NAME
*
*  glp_gmi_cut - generate Gomory's mixed integer cut (core routine)
*
*  SYNOPSIS
*
*  int glp_gmi_cut(glp_prob *P, int j, int ind[], double val[], double
*     phi[]);
*
*  DESCRIPTION
*
*  This routine attempts to generate a Gomory's mixed integer cut for
*  specified integer column (structural variable), whose primal value
*  in current basic solution is integer infeasible (fractional).
*
*  On entry to the routine the basic solution contained in the problem
*  object P should be optimal, and the basis factorization should be
*  valid. The parameter j should specify the ordinal number of column
*  (structural variable x[j]), for which the cut should be generated,
*  1 <= j <= n, where n is the number of columns in the problem object.
*  This column should be integer, non-fixed, and basic, and its primal
*  value should be fractional.
*
*  The cut generated by the routine is the following inequality:
*
*     sum a[j] * x[j] >= b,
*
*  which is expected to be violated at the current basic solution.
*
*  If the cut has been successfully generated, the routine stores its
*  non-zero coefficients a[j] and corresponding column indices j in the
*  array locations val[1], ..., val[len] and ind[1], ..., ind[len],
*  where 1 <= len <= n is the number of non-zero coefficients. The
*  right-hand side value b is stored in val[0], and ind[0] is set to 0.
*
*  The working array phi should have 1+m+n locations (location phi[0]
*  is not used), where m and n is the number of rows and columns in the
*  problem object, resp.
*
*  RETURNS
*
*  If the cut has been successfully generated, the routine returns
*  len, the number of non-zero coefficients in the cut, 1 <= len <= n.
*
*  Otherwise, the routine returns one of the following codes:
*
*  -1    current basis factorization is not valid;
*
*  -2    current basic solution is not optimal;
*
*  -3    column ordinal number j is out of range;
*
*  -4    variable x[j] is not of integral kind;
*
*  -5    variable x[j] is either fixed or non-basic;
*
*  -6    primal value of variable x[j] in basic solution is too close
*        to nearest integer;
*
*  -7    some coefficients in the simplex table row corresponding to
*        variable x[j] are too large in magnitude;
*
*  -8    some free (unbounded) variables have non-zero coefficients in
*        the simplex table row corresponding to variable x[j].
*
*  ALGORITHM
*
*  See glpk/doc/notes/gomory (in Russian). */

#define f(x) ((x) - floor(x))
/* compute fractional part of x */

int glp_gmi_cut(glp_prob *P, int j,
      int ind[/*1+n*/], double val[/*1+n*/], double phi[/*1+m+n*/])
{     int m = P->m;
      int n = P->n;
      GLPROW *row;
      GLPCOL *col;
      GLPAIJ *aij;
      int i, k, len, kind, stat;
      double lb, ub, alfa, beta, ksi, phi1, rhs;
      /* sanity checks */
      if (!(P->m == 0 || P->valid))
      {  /* current basis factorization is not valid */
         return -1;
      }
      if (!(P->pbs_stat == GLP_FEAS && P->dbs_stat == GLP_FEAS))
      {  /* current basic solution is not optimal */
         return -2;
      }
      if (!(1 <= j && j <= n))
      {  /* column ordinal number is out of range */
         return -3;
      }
      col = P->col[j];
      if (col->kind != GLP_IV)
      {  /* x[j] is not of integral kind */
         return -4;
      }
      if (col->type == GLP_FX || col->stat != GLP_BS)
      {  /* x[j] is either fixed or non-basic */
         return -5;
      }
      if (fabs(col->prim - floor(col->prim + 0.5)) < 0.001)
      {  /* primal value of x[j] is too close to nearest integer */
         return -6;
      }
      /* compute row of the simplex tableau, which (row) corresponds
       * to specified basic variable xB[i] = x[j]; see (23) */
      len = glp_eval_tab_row(P, m+j, ind, val);
      /* determine beta[i], which a value of xB[i] in optimal solution
       * to current LP relaxation; note that this value is the same as
       * if it would be computed with formula (27); it is assumed that
       * beta[i] is fractional enough */
      beta = P->col[j]->prim;
      /* compute cut coefficients phi and right-hand side rho, which
       * correspond to formula (30); dense format is used, because rows
       * of the simplex tableau are usually dense */
      for (k = 1; k <= m+n; k++)
         phi[k] = 0.0;
      rhs = f(beta); /* initial value of rho; see (28), (32) */
      for (j = 1; j <= len; j++)
      {  /* determine original number of non-basic variable xN[j] */
         k = ind[j];
         xassert(1 <= k && k <= m+n);
         /* determine the kind, bounds and current status of xN[j] in
          * optimal solution to LP relaxation */
         if (k <= m)
         {  /* auxiliary variable */
            row = P->row[k];
            kind = GLP_CV;
            lb = row->lb;
            ub = row->ub;
            stat = row->stat;
         }
         else
         {  /* structural variable */
            col = P->col[k-m];
            kind = col->kind;
            lb = col->lb;
            ub = col->ub;
            stat = col->stat;
         }
         /* xN[j] cannot be basic */
         xassert(stat != GLP_BS);
         /* determine row coefficient ksi[i,j] at xN[j]; see (23) */
         ksi = val[j];
         /* if ksi[i,j] is too large in magnitude, report failure */
         if (fabs(ksi) > 1e+05)
            return -7;
         /* if ksi[i,j] is too small in magnitude, skip it */
         if (fabs(ksi) < 1e-10)
            goto skip;
         /* compute row coefficient alfa[i,j] at y[j]; see (26) */
         switch (stat)
         {  case GLP_NF:
               /* xN[j] is free (unbounded) having non-zero ksi[i,j];
                * report failure */
               return -8;
            case GLP_NL:
               /* xN[j] has active lower bound */
               alfa = - ksi;
               break;
            case GLP_NU:
               /* xN[j] has active upper bound */
               alfa = + ksi;
               break;
            case GLP_NS:
               /* xN[j] is fixed; skip it */
               goto skip;
            default:
               xassert(stat != stat);
         }
         /* compute cut coefficient phi'[j] at y[j]; see (21), (28) */
         switch (kind)
         {  case GLP_IV:
               /* y[j] is integer */
               if (fabs(alfa - floor(alfa + 0.5)) < 1e-10)
               {  /* alfa[i,j] is close to nearest integer; skip it */
                  goto skip;
               }
               else if (f(alfa) <= f(beta))
                  phi1 = f(alfa);
               else
                  phi1 = (f(beta) / (1.0 - f(beta))) * (1.0 - f(alfa));
               break;
            case GLP_CV:
               /* y[j] is continuous */
               if (alfa >= 0.0)
                  phi1 = + alfa;
               else
                  phi1 = (f(beta) / (1.0 - f(beta))) * (- alfa);
               break;
            default:
               xassert(kind != kind);
         }
         /* compute cut coefficient phi[j] at xN[j] and update right-
          * hand side rho; see (31), (32) */
         switch (stat)
         {  case GLP_NL:
               /* xN[j] has active lower bound */
               phi[k] = + phi1;
               rhs += phi1 * lb;
               break;
            case GLP_NU:
               /* xN[j] has active upper bound */
               phi[k] = - phi1;
               rhs -= phi1 * ub;
               break;
            default:
               xassert(stat != stat);
         }
skip:    ;
      }
      /* now the cut has the form sum_k phi[k] * x[k] >= rho, where cut
       * coefficients are stored in the array phi in dense format;
       * x[1,...,m] are auxiliary variables, x[m+1,...,m+n] are struc-
       * tural variables; see (30) */
      /* eliminate auxiliary variables in order to express the cut only
       * through structural variables; see (33) */
      for (i = 1; i <= m; i++)
      {  if (fabs(phi[i]) < 1e-10)
            continue;
         /* auxiliary variable x[i] has non-zero cut coefficient */
         row = P->row[i];
         /* x[i] cannot be fixed variable */
         xassert(row->type != GLP_FX);
         /* substitute x[i] = sum_j a[i,j] * x[m+j] */
         for (aij = row->ptr; aij != NULL; aij = aij->r_next)
            phi[m+aij->col->j] += phi[i] * aij->val;
      }
      /* convert the final cut to sparse format and substitute fixed
       * (structural) variables */
      len = 0;
      for (j = 1; j <= n; j++)
      {  if (fabs(phi[m+j]) < 1e-10)
            continue;
         /* structural variable x[m+j] has non-zero cut coefficient */
         col = P->col[j];
         if (col->type == GLP_FX)
         {  /* eliminate x[m+j] */
            rhs -= phi[m+j] * col->lb;
         }
         else
         {  len++;
            ind[len] = j;
            val[len] = phi[m+j];
         }
      }
      if (fabs(rhs) < 1e-12)
         rhs = 0.0;
      ind[0] = 0, val[0] = rhs;
      /* the cut has been successfully generated */
      return len;
}

/* eof */
