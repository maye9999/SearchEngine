<%@ page import="org.apache.lucene.search.ScoreDoc" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.print.Doc" %><%--
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

    <title>
        <%=request.getAttribute("currentQuery") %> - 搜索结果
    </title>
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
                            <input type="text" class="form-control" value='<%= request.getAttribute("currentQuery")%>'>
                            <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span></span>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <ul class="nav nav-tabs">

            <li><a id="search-all" class="search-option-tab">所有</a></li>
            <li><a id="search-web" class="search-option-tab">网页</a></li>
            <li><a id="search-doc" class="search-option-tab">文档</a></li>
            <li><a id="fuzzy-search" class="search-option-tab">模糊查询</a></li>
            <li><a id="wildcard-search" class="search-option-tab">通配符搜索</a></li>
            <li><a data-toggle="collapse" data-target="#advanced-search" class="search-option-tab">高级搜索</a></li>
        </ul>

        <div class="tab-content search-result">
            <div id="advanced-search" class="collapse panel panel-primary advance-search-form">
                <div class="panel-heading">
                    高级搜索选项
                </div>
                <div class="panel-body">
                    <form id="advanced-search-form" class="form-horizontal" role="form" action="/result" method="post">
                        <input type="text" name="mode" value="5" style="display: none;">
                        <div class="form-group">
                            <label class="control-label col-md-4">包含以下全部的关键词</label>
                            <div class="col-md-8">
                                <input type="text" class="form-control" name="all-keywords" placeholder="例如: 清华大学 计算机系">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-4">包含以下任意一个关键词</label>
                            <div class="col-md-8">
                                <input type="text" class="form-control" name="any-keywords" placeholder="例如: 清华大学 计算机系">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-4">不包含以下任意一个关键词</label>
                            <div class="col-md-8">
                                <input type="text" class="form-control" name="no-keywords" placeholder="例如: 心理系">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-4">限定搜索指定的网站</label>
                            <div class="col-md-8">
                                <input type="text" class="form-control" name="in-site" placeholder="例如: cs.tsinghua.edu.cn">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-4">搜索文档格式</label>
                            <div class="col-md-8">
                                <select class="form-control" name="doc-type">
                                    <option value="">所有网页文档</option>
                                    <option value="PDF">PDF</option>
                                    <option value="DOC">doc</option>
                                    <option value="DOCX">docx</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-4">关键词位于</label>
                            <div class="col-md-8">
                                <select class="form-control" name="keyword-pos">
                                    <option value="">任何地方</option>
                                    <option value="titleField">仅出现在网站或者文档的标题中</option>
                                    <option value="urlField">仅出现在 URL 中</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" style="margin-bottom: 0">
                            <div class="col-md-offset-10 col-md-2">
                                <button id="advanced-search-submmit" type="submit" class="btn btn-primary">重新搜索</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div>
                <%
                    Document[] docs = (Document []) request.getAttribute("docs");
                    for (Document doc : docs) {
                %>
                <div class="search-result-entry">
                    <div class="entry-name lead text-info">
                        <a href="<%= "http://" + doc.get("urlField") %>">
                            <%= doc.get("titleField") %>
                        </a>
                    </div>
                    <div class="entry-url text-success">
                        <%= doc.get("urlField") %>
                    </div>
                    <div class="entry-content">
                        <%
                            String content = doc.get("contentField");
                            if (content.length() > 100) {
                                out.println(content.substring(0, 100) + "...");
                            } else {
                                out.println(content);
                            }
                        %>
                    </div>
                </div>
                <div class="divider"></div>
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

    <script>
        $(function () {
            var query = '<%= request.getAttribute("currentQuery") %>';
            $('#search-all').click(function () {
                window.location.href = '/result?query=' + query + '&mode=0';
            });
            $('#search-web').click(function () {
                window.location.href = '/result?query=' + query + '&mode=1';
            });
            $('#search-doc').click(function () {
                window.location.href = '/result?query=' + query + '&mode=2';
            });
            $('#fuzzy-search').click(function () {
                window.location.href = '/result?query=' + query + '&mode=3';
            });
            $('#wildcard-search').click(function () {
                window.location.href = '/result?query=' + query + '&mode=4';
            });
            var mode = <%= request.getAttribute("mode") %>;
            if (mode == 0) {
                $('#search-all').parent().addClass('active');
            } else if (mode == 1) {
                $('#search-web').parent().addClass('active');
            } else if (mode == 2) {
                $('#search-doc').parent().addClass('active');
            } else if (mode == 3) {
                $('#fuzzy-search').parent().addClass('active');
            } else if (mode == 4) {
                $('#wildcard-search').parent().addClass('active');
            }

            $('#advanced-search-submmit').click(function () {
                $('#advanced-search-form').submit();
            })
        })
    </script>
</body>
</html>
