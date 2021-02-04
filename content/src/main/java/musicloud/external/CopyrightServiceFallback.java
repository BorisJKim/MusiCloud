package musicloud.external;

public class CopyrightServiceFallback implements CopyrightService {
    @Override
    public void approve(Copyright copyright) {
        //do nothing if you want to forgive it

        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
    }
}
