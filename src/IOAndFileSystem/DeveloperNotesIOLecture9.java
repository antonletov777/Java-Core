package IOAndFileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.StringTokenizer;

@SuppressWarnings("ALL")
public class DeveloperNotesIOLecture9 {
    public static void main(String[] args) {
        FastScanner fastScanner = new FastScanner();

        int n = fastScanner.nextInt();

        System.out.println(n);
    }

    private static class FastScanner {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(Objects.requireNonNull(next()));
        }

        long nextLong() {
            return Long.parseLong(Objects.requireNonNull(next()));
        }

        double nextDouble() {
            return Double.parseDouble(Objects.requireNonNull(next()));
        }
    }
}