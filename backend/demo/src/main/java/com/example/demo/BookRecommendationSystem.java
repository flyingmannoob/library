package com.example.demo;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import com.volcengine.ark.runtime.exception.ArkException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import java.util.concurrent.TimeUnit;

/**
 * 图书推荐系统 - 整合版
 * 包含实体类、数据访问、业务逻辑和控制器功能
 */
@RestController
@RequestMapping("") // 不带前缀的请求路径
public class BookRecommendationSystem {

    // 实体类定义
    @Entity
    @Table(name = "books")
    static class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false, length = 200)
        private String title;
        
        @Column(length = 100)
        private String author;
        
        @Column(columnDefinition = "TEXT")
        private String description;
        
        @Column(name = "category_id")
        private Long categoryId;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "Book{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", categoryId=" + categoryId +
                    '}';
        }
    }

    // 业务逻辑层
    @Service
    static class BookRecommendService {

        private static final Logger logger = LoggerFactory.getLogger(BookRecommendService.class);
        
        @PersistenceContext
        private EntityManager entityManager;
        
        // LLM相关配置 - 与用户提供的示例保持一致
        private static final String LLM_MODEL_NAME = "doubao-seed-1-6-flash-250715";
        private static final String LLM_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3";
        
        // LLM服务客户端
        private ArkService llmService;
        private boolean useLlm = true; // 是否使用LLM服务

        public BookRecommendService() {
            try {
                // 初始化LLM客户端
                initLlmService();
                logger.info("图书推荐服务初始化成功");
            } catch (Exception e) {
                logger.error("初始化LLM服务失败: {}", e.getMessage());
                useLlm = false; // 关闭LLM功能，使用基于规则的推荐
            }
        }
        
        /**
         * 初始化LLM服务客户端 - 按照用户提供的示例代码格式进行实现
         */
        private void initLlmService() {
            try {
                logger.info("开始初始化LLM服务...");
                
                // 从环境变量中读取API Key
                String apiKey = "23ef19ac-42a9-4fc9-a817-04169e24e1b2";
                
                if (apiKey == null || apiKey.isEmpty()) {
                    logger.warn("环境变量ARK_API_KEY未设置，LLM功能将不可用");
                    useLlm = false;
                    return;
                }
                
                // 配置连接池和调度器 - 与用户示例保持一致
                ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
                Dispatcher dispatcher = new Dispatcher();
                
                // 构建LLM服务客户端 - 按照用户示例格式构建
                llmService = ArkService.builder()
                        .dispatcher(dispatcher)
                        .connectionPool(connectionPool)
                        .baseUrl(LLM_BASE_URL)
                        .apiKey(apiKey)
                        .build();
                
                logger.info("LLM服务初始化成功");
            } catch (Exception e) {
                logger.error("LLM服务初始化异常: {}", e.getMessage());
                throw new RuntimeException("LLM服务初始化失败", e);
            }
        }
        
        /**
         * 通过LLM获取图书推荐（跳过数据库，直接转发用户输入）
         * 按照用户提供的示例代码格式进行实现
         * @param userInfo 用户信息
         * @return LLM生成的推荐结果
         */
        private String getRecommendationsFromLlm(String userInfo) throws ArkException {
            try {
                logger.info("[BookRecommendService] 通过LLM服务获取推荐...");
                logger.debug("[BookRecommendService] 用户信息长度: {}", userInfo != null ? userInfo.length() : 0);

                // 从数据库获取书籍列表
                List<Book> books = findAllBooks();
                String systemPrompt;

                if (books.isEmpty()) {
                    logger.warn("[BookRecommendService] 数据库中没有书籍，将推荐经典书籍。");
                    systemPrompt = "你是一个专业的图书推荐助手，根据用户的兴趣和需求推荐合适的书籍。\n" +
                            "请注意：\n" +
                            "1. 请根据用户的阅读偏好推荐相关的书籍\n" +
                            "2. 推荐时请给出具体的推荐理由\n" +
                            "3. 推荐格式：每本书一行，包含书名、作者和推荐理由\n" +
                            "4. 请以简洁明了的方式输出推荐结果\n" +
                            "5. 请推荐经典且广受好评的书籍\n";
                } else {
                    // 将书籍列表格式化为字符串
                    StringBuilder bookListBuilder = new StringBuilder();
                    for (Book book : books) {
                        bookListBuilder.append("- ").append(book.getTitle()).append("\n");
                    }
                    String bookList = bookListBuilder.toString();
                    
                    // 构建系统提示词 
                    systemPrompt = "你是一个专业的图书推荐助手，根据用户的兴趣和需求推荐合适的书籍。\n" +
                            "可推荐的书籍列表如下：\n" + bookList + "\n" +
                            "请注意：\n" +
                            "1. 请根据用户的阅读偏好推荐相关的书籍\n" +
                            "2. 推荐时请给出具体的推荐理由\n" +
                            "3. 推荐格式：每本书一行，包含书名、作者和推荐理由\n" +
                            "4. 请以简洁明了的方式输出推荐结果\n" +
                            "5. 只能从以上列表中推荐书籍\n";
                }
                
                // 构建用户消息 - 直接使用用户输入
                String userMessageContent = userInfo + "\n\n请根据以上用户问答信息，为用户推荐几本合适的书籍，并说明推荐理由。推荐的书籍应该符合用户的兴趣和需求。";
                
                // 记录用户消息长度
                logger.debug("[BookRecommendService] 用户消息内容长度: {}", userMessageContent.length());

                // 构建LLM请求 - 与用户示例保持一致
                final List<ChatMessage> messages = new ArrayList<>();
                messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(systemPrompt)
                        .build());
                messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER)
                        .content(userMessageContent)
                        .build());
                
                // 创建LLM请求 - 按照用户示例格式构建
                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                        // 指定方舟推理接入点 ID
                        .model(LLM_MODEL_NAME)
                        .messages(messages)
                        .build();
                
                // 发送请求并获取结果 - 按照用户示例的链式调用风格
                logger.info("[BookRecommendService] 向LLM服务发送请求...");
                
                // 使用StringBuilder来处理可能的多个选择
                StringBuilder resultBuilder = new StringBuilder();
                llmService.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
                    logger.info("[BookRecommendService] 获取到选择结果");
                    String content = choice.getMessage().getContent().toString();
                    resultBuilder.append(content);
                });
                
                String result = resultBuilder.toString();
                logger.info("[BookRecommendService] 成功获取LLM推荐结果，结果长度: {}", result.length());
                return result;
            } catch (Exception e) {
                logger.error("[BookRecommendService] LLM服务调用异常: {}", e.getMessage());
                throw e; // 重新抛出异常，让上层处理
            }
        }
        
        /**
         * 获取默认推荐
         * @return 默认推荐结果
         */
        public String getBookRecommendations(String userInfo) {
            try {
                logger.info("[BookRecommendService] 开始获取图书推荐...");
                logger.debug("[BookRecommendService] 收到的用户信息长度: {}", userInfo != null ? userInfo.length() : 0);
                
                // 验证输入参数
                if (userInfo == null || userInfo.trim().isEmpty()) {
                    logger.warn("[BookRecommendService] 用户信息为空，无法提供推荐");
                    return "系统提示：用户信息为空，请输入您的偏好以获取推荐。";
                }
                
                // 优先尝试使用LLM获取推荐
                if (useLlm && llmService != null) {
                    try {
                        logger.info("[BookRecommendService] 优先使用LLM服务获取推荐");
                        return getRecommendationsFromLlm(userInfo);
                    } catch (Exception llmEx) {
                        // LLM调用失败，向上抛出异常
                        logger.warn("[BookRecommendService] LLM服务调用失败: {}", llmEx.getMessage());
                        throw llmEx;
                    }
                } else {
                    logger.info("[BookRecommendService] LLM服务不可用，useLlm={}, llmService={}", useLlm, llmService);
                    return "系统提示：智能推荐服务当前不可用，请稍后再试。";
                }
                
            } catch (PersistenceException e) {
                // 数据库连接或查询错误
                logger.error("数据库操作失败: {}", e.getMessage());
                e.printStackTrace();
                
                return "系统提示：数据库连接暂时不可用，请稍后再试。";
            } catch (NullPointerException e) {
                // 空指针异常 - 通常是字段访问错误
                logger.error("数据处理错误: 访问了空对象或字段\n{}", e.getMessage());
                e.printStackTrace();
                
                return "系统提示：数据处理过程中发生错误。";
            } catch (Exception e) {
                // 其他未预期的系统异常
                String errorType = determineErrorType(e);
                logger.error("获取推荐过程中发生系统异常 ({}) : {}", errorType, e.getMessage());
                e.printStackTrace();
                
                // 根据错误类型返回不同的提示信息
                String errorPrompt = getErrorPromptByType(errorType);
                return errorPrompt;
            }
        }
        
        /**
         * 获取书籍列表
         * @return 包含 code、data、message 的标准格式
         */
        public Map<String, Object> getBooks() {
            try {
                // 从数据库获取书籍数据
                List<Book> books = findAllBooks();
                // 转换为书名列表
                List<String> bookTitles = books.stream()
                        .map(Book::getTitle)
                        .collect(Collectors.toList());

                // 返回标准格式
                return Map.of(
                        "code", 20000,
                        "data", bookTitles,
                        "message", "success"
                );
            } catch (Exception e) {
                logger.error("获取书籍列表失败: {}", e.getMessage());
                e.printStackTrace();
                return Map.of(
                        "code", 50000,
                        "message", "获取书籍列表失败：" + e.getMessage()
                );
            }
        }
        
        /**
         * 获取所有书籍
         */
        private List<Book> findAllBooks() {
            try {
                logger.info("[BookRecommendService] 正在从数据库获取所有书籍...");
                TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
                List<Book> books = query.getResultList();
                logger.info("[BookRecommendService] 成功获取 {} 本书", books.size());
                return books;
            } catch (Exception e) {
                logger.error("从数据库获取书籍列表失败: {}", e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        
        /**
         * 根据异常类型确定错误类型
         * @param e 异常对象
         * @return 错误类型描述
         */
        private String determineErrorType(Exception e) {
            // 检查异常链中的根本原因
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof ArkException) {
                    return "LLM服务错误";
                } else if (cause instanceof SocketTimeoutException) {
                    return "服务响应超时";
                } else if (cause instanceof ConnectException) {
                    return "网络连接失败";
                }
                cause = cause.getCause();
            }
            return "未知错误";
        }
        
        /**
         * 根据错误类型获取对应的提示信息
         * @param errorType 错误类型
         * @return 错误提示信息
         */
        private String getErrorPromptByType(String errorType) {
            switch (errorType) {
                case "LLM服务错误":
                    return "系统提示：智能推荐服务暂时不可用，请设置ARK_API_KEY后重试。";
                case "服务响应超时":
                    return "系统提示：服务响应超时，请检查网络连接后重试。";
                case "网络连接失败":
                    return "系统提示：网络连接失败，请检查您的网络设置。";
                default:
                    return "系统提示：推荐服务暂时不可用";
            }
        }
    }

    // 控制器方法
    private static final Logger logger = LoggerFactory.getLogger(BookRecommendationSystem.class);
    private final BookRecommendService bookRecommendService;

    @Autowired
    public BookRecommendationSystem(BookRecommendService bookRecommendService) {
        this.bookRecommendService = bookRecommendService;
    }

    /**
     * 获取书籍列表
     * @return 包含 code、data、message 的标准格式
     */
    @GetMapping("/books")
    public Map<String, Object> getBooks() {
        // 调用服务层方法
        return bookRecommendService.getBooks();
    }

    /**
     * 获取书籍推荐 - 简化版本
     * @param requestBody 包含用户问答信息的请求体
     * @return 包含推荐结果的响应
     */
    @PostMapping("/books/recommend")
    public Map<String, Object> recommendBooks(@RequestBody Map<String, String> requestBody) {
        try {
            String userInfo = requestBody.getOrDefault("userInfo", "用户未提供具体阅读偏好");
            String recommendations = bookRecommendService.getBookRecommendations(userInfo);
            return Map.of("code", 20000, "data", recommendations, "message", "推荐成功");
        } catch (Exception e) {
            logger.error("推荐请求处理失败", e);
            return Map.of("code", 50000, "data", "获取推荐失败，请稍后重试", "message", e.getMessage());
        }
    }
}