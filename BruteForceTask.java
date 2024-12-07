import java.util.concurrent.Callable;

public class BruteForceTask implements Callable<String> {
    private final String prefixRange;
    private final String targetHash;
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyz";

    public BruteForceTask(String prefixRange, String targetHash) {
        this.prefixRange = prefixRange;
        this.targetHash = targetHash;
    }

    @Override
    public String call() {
        for (char prefix : prefixRange.toCharArray()) {
            String result = bruteForce(String.valueOf(prefix), 5);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private String bruteForce(String prefix, int maxLength) {
        char[] current = new char[maxLength];
        for (int i = 0; i < prefix.length(); i++) {
            current[i] = prefix.charAt(i);
        }
        for (int i = prefix.length(); i < maxLength; i++) {
            current[i] = CHARSET.charAt(0);
        }
    
        while (true) {
            String attempt = new String(current);
            String computedHash = HashUtils.hash(attempt, targetHash.length() == 32 ? "MD5" : "SHA-256");
            if (computedHash.equals(targetHash)) {
                return attempt;
            }
    
            int index = maxLength - 1;
            while (index >= prefix.length()) {
                if (current[index] == CHARSET.charAt(CHARSET.length() - 1)) {
                    current[index] = CHARSET.charAt(0);
                    index--;
                } else {
                    current[index] = CHARSET.charAt(CHARSET.indexOf(current[index]) + 1);
                    break;
                }
            }
    
            if (index < prefix.length()) {
                break;
            }
        }
        return null;
    }
}
