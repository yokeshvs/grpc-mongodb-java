package com.yo.prototype.client;

import com.yo.prototype.Blog;
import com.yo.prototype.BlogServiceGrpc;
import com.yo.prototype.CreateBlogRequest;
import com.yo.prototype.CreateBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcMongoClient {
    ManagedChannel channel;
    BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub;

    private void initialize() {
        //Create Channel
        System.out.println("Creating channel");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // For development purpose, disabling SSL
                .build();

        //Create stub
        blogServiceBlockingStub = BlogServiceGrpc.newBlockingStub(channel);
    }

    private void exit() {
        //Shutdown Channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void createAuthor(String authorName, String title, String content) {
        CreateBlogResponse createBlogResponse = blogServiceBlockingStub.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setAuthorName(authorName)
                        .setTitle(title)
                        .setContent(content)
                        .build())
                .build());
        System.out.println(createBlogResponse.toString());
    }

    public static void main(String[] args) {
        GrpcMongoClient grpcMongoClient = new GrpcMongoClient();
        grpcMongoClient.initialize();

        grpcMongoClient.createAuthor("Luther", "Animal Kingdom", "Once upon a time, there lived a lion, king of the jungle!");
        grpcMongoClient.createAuthor("Sam", "Lost in Jungle", "Blah blah blah, blah blah, blah blah blah blah");

        grpcMongoClient.exit();
    }
}
