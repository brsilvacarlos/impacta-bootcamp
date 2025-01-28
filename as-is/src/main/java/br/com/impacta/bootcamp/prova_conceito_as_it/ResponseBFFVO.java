package br.com.impacta.bootcamp.prova_conceito_as_it;
import java.util.List;

public class ResponseBFFVO {
    private List<ServiceResponse> serviceResponses;

    // Construtor, getters e setters

    public ResponseBFFVO(List<ServiceResponse> serviceResponses) {
        this.serviceResponses = serviceResponses;
    }

    public List<ServiceResponse> getServiceResponses() {
        return serviceResponses;
    }

    public void setServiceResponses(List<ServiceResponse> serviceResponses) {
        this.serviceResponses = serviceResponses;
    }

    // Classe interna para armazenar as informações de cada serviço
    public static class ServiceResponse {
        private String serviceName;
        private String response;
        private long responseTime;

        // Construtor, getters e setters

        public ServiceResponse(String serviceName, String response, long responseTime) {
            this.serviceName = serviceName;
            this.response = response;
            this.responseTime = responseTime;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }
    }
}
