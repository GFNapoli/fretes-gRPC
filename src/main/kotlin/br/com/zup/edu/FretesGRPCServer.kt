package br.com.zup.edu

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGRPCServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGRPCServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete: $request")

        val cep = request?.cep

        if(cep == null || cep.isBlank()){
            val e = Status.INVALID_ARGUMENT
                .withDescription("CEP invalido")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        if(!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            val e = Status.INVALID_ARGUMENT
                .withDescription("Fortamto Cep invalido")
                .augmentDescription("O formato deve ser 99999-999")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        //teste de erro
        if(cep.endsWith("333")){
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuario não pode acessar esse recurso")
                .addDetails(
                    Any.pack(ErroDatails.newBuilder().setCode(401)
                        .setMessage("acesso negado")
                        .build())
                )
                .build()
            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        var valorFrete = 0.0

        try {
            valorFrete = Random.nextDouble(0.0,140.0)
        }catch (e: Exception){
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)  // anexado ao status, não é enviado ao cliente
                .asRuntimeException())
        }

        val response =  CalculaFreteResponse.newBuilder()
            .setValor(valorFrete)
            .setCep(request!!.cep)
            .build()

        logger.info("response: $response")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}