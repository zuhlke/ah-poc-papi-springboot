import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {
    public static void main(String[] args) throws UnirestException {
        String originOfEndpointUnderTest = args[0];
        String consumerName = "ah-poc-papi-springboot";
        System.out.println("Running contract tests for consumer: '" + consumerName + "' against producer endpoint '" + originOfEndpointUnderTest + "'");
        ContractTester contractTester = new CreditCardSapiContractTester(originOfEndpointUnderTest);
        contractTester.runTests();
        System.out.println("Contract Tests Passed!");
    }
}