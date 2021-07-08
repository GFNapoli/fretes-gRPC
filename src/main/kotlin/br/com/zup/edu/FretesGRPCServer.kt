package br.com.zup.edu

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGRPCServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGRPCServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete: $request")

        val response =  CalculaFreteResponse.newBuilder()
            .setValor(Random.nextDouble(0.0,140.0))
            .setCep(request!!.cep)
            .build()

        logger.info("response: $response")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}