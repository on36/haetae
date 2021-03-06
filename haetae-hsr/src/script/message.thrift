namespace java com.on36.haetae.rpc.thrift

service  SendService {
  Result send(1:Message message)
}

struct Message {
 1: optional string id,
 2: string topic 
 3: binary content  
 4: optional i64 createdTime
 5: optional string source
}
struct Result {
 1: string id
 2: ResultType type
 3: optional binary data 
}


enum ResultType {
 SUCCESS = 1,
 FAILURE = 0
}