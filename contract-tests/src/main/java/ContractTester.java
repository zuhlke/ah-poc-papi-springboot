import com.mashape.unirest.http.exceptions.UnirestException;

public interface ContractTester {
    void runTests() throws UnirestException;
}
