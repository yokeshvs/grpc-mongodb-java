package com.yo.prototype.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yo.prototype.Blog;
import com.yo.prototype.BlogServiceGrpc;
import com.yo.prototype.CreateOrUpdateBlogRequest;
import com.yo.prototype.CreateOrUpdateBlogResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {
    private final MongoClient mongoClient = MongoClients.create("mongodb+srv://mongouser:7dD4nDeDbSl5iA2k@user-cluster-01-6ph2d.azure.mongodb.net/<dbname>?retryWrites=true&w=majority");
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("reimagine");
    private final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("grpc-blog");

    @Override
    public void createBlog(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();
        if (blog != null) {
            Document document = new Document();
            document.append("authorName", blog.getAuthorName())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent());

            mongoCollection.insertOne(document);

            responseObserver.onNext(CreateOrUpdateBlogResponse.newBuilder()
                    .setBlog(blog.toBuilder().setId(document.getObjectId("_id").toString()))
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Input request is invalid").asRuntimeException());
        }
    }

    @Override
    public void getBlogByAuthor(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        Document document = mongoCollection.find(eq("authorName", Objects.requireNonNull(request).getBlog().getAuthorName())).first();
        if (document != null) {
            responseObserver.onNext(projectResponse(document));
            responseObserver.onCompleted();
        } else
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
    }

    @Override
    public void getBlogById(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        Document document = mongoCollection.find(eq("_id", new ObjectId(Objects.requireNonNull(request).getBlog().getId()))).first();
        if (document != null) {
            responseObserver.onNext(projectResponse(document));
            responseObserver.onCompleted();
        } else
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
    }

    @Override
    public void getBlogByTitle(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        Document document = mongoCollection.find(eq("title", Objects.requireNonNull(request).getBlog().getTitle())).first();
        if (document != null) {
            responseObserver.onNext(projectResponse(document));
            responseObserver.onCompleted();
        } else
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
    }

    @Override
    public void updateBlogByAuthor(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = new Document();
        document.append("authorName", blog.getAuthorName())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());
        document = mongoCollection.findOneAndReplace(eq("authorName", blog.getAuthorName()), document);
        document = mongoCollection.find(eq("_id", Objects.requireNonNull(document).getObjectId("_id"))).first();
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlogById(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = new Document();
        document.append("authorName", blog.getAuthorName())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());
        document = mongoCollection.findOneAndReplace(eq("_id", new ObjectId(blog.getId())), document);
        document = mongoCollection.find(eq("_id", Objects.requireNonNull(document).getObjectId("_id"))).first();
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlogByTitle(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = new Document();
        document.append("authorName", blog.getAuthorName())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());
        document = mongoCollection.findOneAndReplace(eq("title", blog.getTitle()), document);
        document = mongoCollection.find(eq("_id", Objects.requireNonNull(document).getObjectId("_id"))).first();
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlogByAuthor(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = mongoCollection.findOneAndDelete(eq("authorName", blog.getAuthorName()));
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlogById(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = mongoCollection.findOneAndDelete(eq("_id", new ObjectId(blog.getId())));
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlogByTitle(CreateOrUpdateBlogRequest request, StreamObserver<CreateOrUpdateBlogResponse> responseObserver) {
        if (request == null || request.getBlog() == null)
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());

        Blog blog = Objects.requireNonNull(request).getBlog();
        Document document = mongoCollection.findOneAndDelete(eq("title", blog.getTitle()));
        responseObserver.onNext(projectResponse(Objects.requireNonNull(document)));
        responseObserver.onCompleted();
    }

    private CreateOrUpdateBlogResponse projectResponse(Document document) {
        return CreateOrUpdateBlogResponse.newBuilder()
                .setBlog(extractBlog(document))
                .build();
    }

    private Blog extractBlog(Document document) {
        return Blog.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setContent(document.getString("content"))
                .setTitle(document.getString("title"))
                .setAuthorName(document.getString("authorName"))
                .build();
    }
}
