import java.io.*;
import java.util.*;

class TomAndJerry {

	int M, N;
	char [] ORIG, B;
	int J, G;
	
	int SZ = 200;
	int [] DOTS;;
	
	public TomAndJerry () throws IOException {
		M = sc.nextInt();
		N = sc.nextInt();
		ORIG = new char [M*N];
		DOTS = new int [N*M];
		Arrays.fill(DOTS, -1);
		
		char [][] C = new char[M][];
		for (int i = 0, d = 0; i < M; ++i) {
			try {
				C[i] = sc.nextLine().toCharArray();
			} catch (NullPointerException e) {
				C[i] = new char [N];
				Arrays.fill(C, '.');
			}
			for (int j = 0; j < N; ++j) {
				int x = N*i+j;
				ORIG[x] = C[i][j];
				if (ORIG[x] == '*')
					G = x;
				if (ORIG[x] == '.' || ORIG[x] == '*')
					DOTS[d++] = x;
			}
		}

		List<Integer> best = new ArrayList<Integer>();
		//for (int i = -1; i <= 1; ++i) {
		int i = 0;
			J = G;
			B = Arrays.copyOf(ORIG, M*N);
			List<Integer> sol = solve(i);
			if (best.size() == 0 || (sol.size() > 0 && sol.size() < best.size()))
				best = sol;
		//}
		
		print(best.size());
		for (int h : best)
			print((1+h/N) + " " + (1+h%N));

//		print("");
//		for (int m = 0; m < M; ++m) {
//			char [] b = new char[N];
//			for (int n = 0; n < N; ++n)
//				b[n] = B[m*N+n];
//			print(String.valueOf(b));
//		}		
	}

	enum Status {
		DONE,
		DEAD,
		CONTINUE,
		LOST
	};
	
	int Z;
	int [] PATH = new int[5];
	
	List<Integer> solve(int mode) throws IOException {

		List<Integer> sol = new ArrayList<Integer>();
		
		out: for (int k = 0; k < SZ; ++k) {
			Status stat = bfs(k > 0, mode);
			if (stat == Status.DONE)
				break;
			
			if (stat == Status.LOST)
				return new ArrayList<Integer>();
				//exit("Too bad");

			// TOM			
			if (!QUE.isEmpty()) {
				int x = QUE.poll();
				int y = dotIt(x, QUE, PATH);
				if (ok(y)) {
					B[y] = '#';
					sol.add(y);
					continue out;
				} else
					QUE.clear();
			}
			
			int x = Z;
			B[x] = '#';
			sol.add(x);
		}

		if (sol.size() > SZ)
			sol.clear();

		return sol;
	}
	
	Random r = new Random();
	
	Status bfs(boolean jerryMoves, int mode) {
		int [] V = new int [M*N], Q = new int [M*N];
		Arrays.fill(V, -1);
		
		int fi = 0, la = 0;
		Q[la++] = J;
		V[J] = J;
		Status stat = Status.DONE;
		
		out: while (fi < la) {
			int z = Q[fi++];
			int Y [] = { z-N, z+1, z-1, z+N };
			clear(Y);
			for (int iy = 0; iy < 4; ++iy) {
				int y = Y[iy];
				if (ok(y) && V[y] == -1) {
					Q[la++] = y;
					V[y] = z;
				} else if (door(y)) {
					stat = Status.CONTINUE;
					if (jerryMoves) {
						int w = z;
						while (V[w] != J)
							w = V[w];
						if (w == z) // We lost! 
							return Status.LOST;
						else {
							B[J] = '.';
							J = w;
							B[J] = '*';
						}
					}
					
					int [] path = new int [M*N];
					path [0] = z;
					int len;
					for (len = 1; len < M*N; ++len) {
						path[len] = V[path[len-1]];
						if (path[len] == J)
							break;
					}
					
					PATH = Arrays.copyOfRange(path, Math.max(0,len-5), len);
					Z = z;
					
					if (QUE.isEmpty()) {
						if (len < 5)
							Z = path[len-1];

						if (len == 2 ) {
							int MCNT = 0;
							int [] CNT = new int [4];
							int [] D = { -N, +1, -1, +N };
							for (int i = 0; i < 4; ++i) {
								int d = D[i];
								int K = J + d;
								if (!ok(K))
									continue;
								int cnt = 0;
								for (int m = 0; m < 4; ++m) {
									boolean mcnt = false;
									if (ok(K + D[m]))
										for (int n = 0; n < 4; ++n)
											if (door(K + D[m] + D[n])) {
												int a = K, b = K + D[m];
												if (a == b + 1 && a % N == 0)
													continue;
												if (b == a + 1 && b % N == 0)
													continue;												
												a = K + D[m]; b = K + D[m] + D[n];
												if (a == b + 1 && a % N == 0)
													continue;
												if (b == a + 1 && b % N == 0)
													continue;												
												mcnt = true;
											}
									if (mcnt) ++cnt;
								}
								CNT[i] = cnt;
								MCNT = Math.max(MCNT, CNT[i]);
							}

							int j = -1, k = -1;
							for (int i = 0; i < 4; ++i) {
								if (j == -1 && CNT[i] > 0)
									j = i;
								else if (k == -1 && CNT[i] > 0)
									k = i;
							}
							
							Z = path[1];
							
							if (CNT[j] > 2)
								Z = path[1];
							if (CNT[j] == 2 && k >= 0 && CNT[k] >= 2)
								Z = z;
							if (CNT[j] == 1 && k >= 0 && CNT[k] >= 2)
								Z = J + D[k];
						}
						
						if (len >= 5) {
							int sz = Math.min(len, 5);
							int [] places = new int [sz+1];
							Arrays.fill(places, -1);
							
							int pl = 0;
							for (int i = 1; i <= 2; ++i) {
								int x = PATH[i], a = PATH[i-1], b = i == 2 && sz == 3 ? J : PATH[i+1];
								int X[] = { x-N, x+1, x-1, x+N };
								clear(X);
								for (int ix = 0; ix < 4; ++ix)
									if (X[ix] != a && X[ix] != b && pl <= sz-1 && ok(X[ix]))
										places[pl++] = X[ix];
							}
							
							places[sz-1] = PATH[0];
							places[sz] = sz == 3 ? J : PATH[3];
							
							if (pl <= sz-1) {
								for (int i = 0; i <= sz; ++i)
									QUE.add(places[i]);
								stat = Status.DEAD;
							} else
								for (int i = 0; i < sz; ++i)
									QUE.add(places[i]);
						}
					}
					
					break out;
				}
			}
		}
		return stat;
	}

	Queue<Integer> QUE = new LinkedList<Integer>();
	
	int dotIt(int x, Queue<Integer> places, int [] path) {
		if (ok(x))
			return x;
		else {
			for (int j = 0; j < DOTS.length; ++j) {
				int y = DOTS[j];
				if (ok(y)) {
					boolean good = true;
					for (int p : places)
						if (y == p)
							good = false;
					for (int p : path)
						if (y == p)
							good = false;
					if (good)
						return y;
				}
			}
			return -1;
		}
	}

	
	boolean ok(int x) {
		return x >= 0 && x < N*M && B[x] == '.';
	}

	boolean door(int x) {
		return x >= 0 && x < N*M && B[x] == '0';
	}

	void clear(int [] A) {
		if (A[1] % N == 0)
			A[1] = -1;
		if (A[2] % N == N-1)
			A[2] = -1;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
		
	static MyScanner sc;
	static long _t;
	
	static void print (Object o) {
		System.out.println(o);
	}
	
	static void exit (Object o) {
		print(o);
		//print2((millis() - t) / 1000.0);
		System.exit(0);
	}
	
	static void run () throws IOException {
		sc = new MyScanner ();
//		for (;;) {
//		Gen.main(null);
//			sc = new MyScanner (new FileReader("gen.txt"));
			new TomAndJerry();
//		}	
	}
	
	public static void main(String[] args) throws Throwable {
		run();
	}
	
	static long millis() {
		return System.currentTimeMillis();
	}
	
	static void start() {
		_t = millis();
	}
	
	static class MyScanner {
		String next() throws IOException {
			newLine();
			return line[index++];
		}
		
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
		
		double nextDouble() throws IOException {
			return Double.parseDouble(next());
		}
		
		String nextLine() throws IOException {
			line = null;
			return r.readLine();
		}
		
		//////////////////////////////////////////////
		
		private final BufferedReader r;

		MyScanner () throws IOException {
			this(new InputStreamReader(System.in));
		}
		
		MyScanner(Reader r) throws IOException { 
			this.r = new BufferedReader(r);
		}
		
		private String [] line;
		private int index;

		private void newLine() throws IOException {
			if (line == null || index == line.length) {
				line = r.readLine().split(" ");
				index = 0;
			}
		}		
	}	
}
