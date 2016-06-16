<%--
  Created by IntelliJ IDEA.
  User: lzhengning
  Date: 6/16/16
  Time: 9:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/css/bootstrap.min.css">

    <!-- font-awesome -->
    <link rel="stylesheet" href="/css/font-awesome.min.css">

    <link rel="stylesheet" href="/css/result.css">

    <title>清华大学 - 搜索结果</title>
</head>

<body>


    <nav class="navbar navbar-default" role="navigation">
        <div class="container">

            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapsible">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">ThuGo</a>
            </div>

            <div class="navbar-collapse collapse" id="navbar-collapsible">

                <form class="navbar-form">
                    <div class="form-group">
                        <div class="input-group">
                            <input type="text" class="form-control" value="thu">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span></span>
                        </div>
                    </div>
                </form>
            </div>

        </div>
    </nav>

    <div class="container">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#search-all">All</a></li>
            <li><a data-toggle="tab" href="#search-web">Web</a></li>
            <li><a data-toggle="tab" href="#search-document">Document</a></li>
            <li><a data-toggle="collapse" href="#advanced-search">Advanced Search</a></li>
        </ul>
        <div id="advanced-search" class="collapse">
            Lorem ipsum dolor text....
        </div>

        <div class="tab-content search-result">
            <div id="search-all" class="tab-pane fade in active">
                <% for (int i = 0; i < 5; ++i) { %>
                <div class="search-result-entry">
                    <div class="entry-name lead text-info">
                        <a href="www.tsinghua.edu.cn">
                            清华大学 - Tsinghua University
                        </a>
                    </div>
                    <div class="entry-url text-success">www.tsinghua.edu.cn/</div>
                    <div class="entry-content">邱勇校长：有你的清华会更美——致2016年高考考生的邀请信. 水木清华，钟灵毓秀。 在这个美好的日子里，我代表<mark>清华大学</mark>向你发出诚挚的邀请，欢迎你加入清华人的 ...</div>
                </div>
                <% } %>
            </div>
            <div id="search-web" class="tab-pane fade">
                <% for (int i = 0; i < 10; ++i) { %>
                <div class="search-result-entry">
                    <div class="entry-name lead text-info">
                        <a href="www.tsinghua.edu.cn">
                            清华大学 - Tsinghua University
                        </a>
                    </div>
                    <div class="entry-url text-success">www.tsinghua.edu.cn/</div>
                    <div class="entry-content">邱勇校长：有你的清华会更美——致2016年高考考生的邀请信. 水木清华，钟灵毓秀。 在这个美好的日子里，我代表<mark>清华大学</mark>向你发出诚挚的邀请，欢迎你加入清华人的 ...</div>
                </div>
                <% } %>
            </div>
            <div id="search-document" class="tab-pane fade">
                <% for (int i = 0; i < 10; ++i) { %>
                <div class="search-result-entry">
                    <div class="entry-name lead text-info">
                        <a href="www.tsinghua.edu.cn">
                            清华大学 - Tsinghua University
                        </a>
                    </div>
                    <div class="entry-url text-success">www.tsinghua.edu.cn/</div>
                    <div class="entry-content">邱勇校长：有你的清华会更美——致2016年高考考生的邀请信. 水木清华，钟灵毓秀。 在这个美好的日子里，我代表<mark>清华大学</mark>向你发出诚挚的邀请，欢迎你加入清华人的 ...</div>
                </div>
                <% } %>
            </div>
        </div>

        <nav>
            <ul class="pagination">
                <li class="disabled">
                    <a href="#" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <li class="active"><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#">5</a></li>
                <li>
                    <a href="#" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>


    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/js/bootstrap.min.js"></script>
</body>
</html>
