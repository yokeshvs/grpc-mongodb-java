package com.yo.prototype.client;

import com.google.protobuf.Empty;
import com.yo.prototype.Blog;
import com.yo.prototype.BlogServiceGrpc;
import com.yo.prototype.CreateOrUpdateBlogRequest;
import com.yo.prototype.CreateOrUpdateBlogResponse;
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

    private void createBlog(String authorName, String title, String content) {
        CreateOrUpdateBlogResponse createOrUpdateBlogResponse = blogServiceBlockingStub.createBlog(CreateOrUpdateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setAuthorName(authorName)
                        .setTitle(title)
                        .setContent(content)
                        .build())
                .build());
        System.out.println(createOrUpdateBlogResponse.toString());
    }

    private void getBlogByAuthor(String authorName) {
        CreateOrUpdateBlogResponse createOrUpdateBlogResponse = blogServiceBlockingStub.getBlogByAuthor(CreateOrUpdateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setAuthorName(authorName)
                        .build())
                .build());
        System.out.println(createOrUpdateBlogResponse.toString());
    }

    private void getBlogById(String id) {
        CreateOrUpdateBlogResponse createOrUpdateBlogResponse = blogServiceBlockingStub.getBlogById(CreateOrUpdateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setId(id)
                        .build())
                .build());
        System.out.println(createOrUpdateBlogResponse.toString());
    }

    private void getBlogByTitle(String title) {
        CreateOrUpdateBlogResponse createOrUpdateBlogResponse = blogServiceBlockingStub.getBlogByTitle(CreateOrUpdateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setTitle(title)
                        .build())
                .build());
        System.out.println(createOrUpdateBlogResponse.toString());
    }

    private void listAllBlogs() {
        blogServiceBlockingStub.listAllBlogs(Empty.newBuilder().build()).forEachRemaining(createOrUpdateBlogResponse -> {
            System.out.println(createOrUpdateBlogResponse.getBlog().toString());
        });
    }

    public static void main(String[] args) {
        GrpcMongoClient grpcMongoClient = new GrpcMongoClient();
        grpcMongoClient.initialize();

        grpcMongoClient.createBlog("Luther", "Animal Kingdom", "Once upon a time, there lived a lion, king of the jungle!");
        grpcMongoClient.createBlog("Sam", "Lost in Jungle", "Blah blah blah, blah blah, blah blah blah blah");

//        grpcMongoClient.getBlogByAuthor("Sam");
//        grpcMongoClient.getBlogByAuthor("Sams");
//
//        grpcMongoClient.getBlogById("123");
//        grpcMongoClient.getBlogByTitle("Lost in Jungle");

        grpcMongoClient.listAllBlogs();

        grpcMongoClient.exit();
    }
}
