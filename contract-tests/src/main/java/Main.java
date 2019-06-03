import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {
    public static void main(String[] args) throws UnirestException {
        String originOfEndpointUnderTest = args[0];
        ContractTester contractTester = new CreditCardSapiContractTester(originOfEndpointUnderTest);
        contractTester.runTests();
    }
}