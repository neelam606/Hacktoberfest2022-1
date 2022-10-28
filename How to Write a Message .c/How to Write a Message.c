#include <iostream>

#include <bits/stdc++.h>

using namespace std;

typedef long long ll;



const int MOD = 1e9 + 7;



int x, y, n, m;

int DP[20][1<<19][20];



int fun(int p, int mask, int k) {



    if(k > m)

        return fun(p, mask, m-1);

    if(p > n) return 1;

    

    if(DP[p][mask][k] != -1)

        return DP[p][mask][k];

    int ans = 0;

    

    int temp = mask;

    temp = mask<<1;

    temp %= (1<<m);



    if(k < m-1 || temp < x || temp > y) {

        ans += fun(p+1, temp, k+1);

        ans %= MOD;

    }

    temp++;

    

    if(k < m-1 || temp < x || temp > y) {

        ans += fun(p+1, temp, k+1);

        ans %= MOD;

    }



    ans += ((ll)24 * fun(p+1, 0, 0)) % MOD;

    ans %= MOD;



    return DP[p][mask][k] = ans;

}



int main() {

        //get_inout_fast

    #ifndef ONLINE_JUDGE

        freopen("in.txt", "r", stdin);

    freopen("out.txt", "w", stdout);

#endif

    //your code goes here

    int T=1;  scanf("%d", &T);



    while(T--) {

        

        memset(DP, -1, sizeof DP);

        scanf("%d %d", &n, &m);

        string X, Y;  cin>>X>>Y;

        x = y = 0;



        for(int i=0; i<m; i++) {

            x = (x<<1) + X[i] - 'A';

            y = (y<<1) + Y[i] - 'A';

        }



        int ans = fun(1, 0, 0);

        printf("%d\n", ans);

    }



    return 0;

}
