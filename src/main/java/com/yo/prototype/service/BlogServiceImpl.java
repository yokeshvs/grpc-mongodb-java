package com.yo.prototype.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yo.prototype.Blog;
import com.yo.prototype.BlogServiceGrpc;
import com.yo.prototype.CreateBlogRequest;
import com.yo.prototype.CreateBlogResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.bson.Document;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {
    private final MongoClient mongoClient = MongoClients.create("mongodb+srv://mongouser:7dD4nDeDbSl5iA2k@user-cluster-01-6ph2d.azure.mongodb.net/<dbname>?retryWrites=true&w=majority");
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("reimagine");
    private final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("grpc-blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();
        if (blog != null) {
            Document document = new Document();
            document.append("authorName", blog.getAuthorName())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent());

            mongoCollection.insertOne(document);

            responseObserver.onNext(CreateBlogResponse.newBuilder()
                    .setBlog(blog.toBuilder().setId(document.getObjectId("_id").toString()))
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Input request is invalid").asRuntimeException());
        }
    }
}
