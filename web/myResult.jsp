<%@ page import="org.apache.lucene.search.ScoreDoc" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.print.Doc" %>
<%@ page import="org.apache.lucene.search.Query" %><%--
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
        <%
            int mode = (Integer) request.getAttribute("mode");
            if (mode != 5) {
                out.println((String) request.getAttribute("currentQuery") + " - 搜索结果");
            } else {
                out.println("高级搜索 - 搜索结果");
            }
        %>
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
            <a class="navbar-brand brand" href="/search">ThuGo</a>
        </div>

        <div class="navbar-collapse collapse" id="navbar-collapsible">
            <form class="navbar-form" action="result" method="get">
                <div class="form-group">
                    <div class="input-group">
                        <% if (mode != 5) {%>
                            <input required type="text" id="top-searcher" class="form-control top-search" name="query" value='<%= request.getAttribute("currentQuery")%>'>
                        <% } else { %>
                            <input required type="text" id="top-searcher" class="form-control top-search" name="query">
                        <% }        %>

                        <input type="text" name="page" value="0" style="display: none;">
                        <span class="input-group-btn"><button class="btn btn-default" type="submit"><i class="fa fa-search"></i></button></span>
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
        <li><a data-toggle="collapse" data-target="#fuzzy-search" class="search-option-tab">模糊查询</a></li>
        <li><a data-toggle="collapse" data-target="#wildcard-search" class="search-option-tab">通配符搜索</a></li>
        <li><a data-toggle="collapse" data-target="#advanced-search" class="search-option-tab">高级搜索</a></li>
    </ul>

    <div class="tab-content search-result">
        <div id="fuzzy-search" class="collapse panel panel-primary advance-search-form">
            <div class="panel-heading">
                模糊搜索
            </div>
            <div class="panel-body">
                <form id="fuzzy-search-form" class="form-horizontal" role="form" action="/result" method="get">
                    <input type="text" name="mode" value="3" style="display: none;">
                    <input type="text" name="page" value="0" style="display: none;">
                    <div class="form-group">
                        <label class="control-label col-md-4">模糊查询关键词</label>
                        <div class="col-md-8">
                            <input required type="text" class="form-control" name="query" placeholder="例如: Tssnghua Unaversity">
                        </div>
                    </div>
                    <div class="form-group" style="margin-bottom: 0">
                        <div class="col-md-offset-10 col-md-2">
                            <button id="fuzzy-search-submmit" type="submit" class="btn btn-primary">重新搜索</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div id="wildcard-search" class="collapse panel panel-primary advance-search-form">
            <div class="panel-heading">
                通配符搜索
            </div>
            <div class="panel-body">
                <form id="wildcard-search-form" class="form-horizontal" role="form" action="/result" method="get">
                    <input type="text" name="mode" value="4" style="display: none;">
                    <input type="text" name="page" value="0" style="display: none;">
                    <div class="form-group">
                        <label class="control-label col-md-4">通配符（正则表达式）查询</label>
                        <div class="col-md-8">
                            <input required type="text" class="form-control" name="query" placeholder="例如: T?inghua Un*ty">
                        </div>
                    </div>
                    <div class="form-group" style="margin-bottom: 0">
                        <div class="col-md-offset-10 col-md-2">
                            <button id="wildcard-search-submmit" type="submit" class="btn btn-primary">重新搜索</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div id="advanced-search" class="collapse panel panel-primary advance-search-form">
            <div class="panel-heading">
                高级搜索选项
            </div>
            <div class="panel-body">
                <form id="advanced-search-form" class="form-horizontal" role="form" action="/result" method="get">
                    <input type="text" name="mode" value="5" style="display: none;">
                    <input type="text" name="page" value="0" style="display: none;">
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
                                <option value="title">仅出现在网站或者文档的标题中</option>
                                <option value="h1">仅出现在网页的 &lt;h1&gt; 中</option>
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
                String[] titles = (String []) request.getAttribute("titles");
                String[] contents = (String []) request.getAttribute("contents");
                String[] urls = (String []) request.getAttribute("urls");
                String[] types = (String []) request.getAttribute("types");
                for (int i = 0; i < titles.length; ++i) {
            %>
            <div class="search-result-entry">
                <div class="entry-name lead text-info">
                    <a href="<%= "http://" + urls[i] %>">
                        <%
                            if (!types[i].equals("HTML")) {
                                out.print("[" + types[i] + "]");
                            }
                            if (titles[i].length() < 80) {
                                out.println(titles[i]);
                            } else {
                                out.println(titles[i].substring(0, 80) + "...");
                            }
                        %>
                    </a>
                </div>
                <div class="entry-url text-success">
                    <%
                        if (urls[i].length() < 90) {
                            out.println(urls[i]);
                        } else {
                            out.println(urls[i].substring(0, 90) + "...");
                        }
                    %>
                </div>
                <div class="entry-content">
                    <%= contents[i] %>
                </div>
            </div>
            <div class="divider"></div>
            <% } %>
            <% if (titles.length == 0) { %>
            <h3 class="text-warning"> 没有找到搜索结果 </h3>
            <% } %>
        </div>
    </div>

    <nav>
        <ul class="pagination" id="manager_pagination">

        </ul>
    </nav>
</div>


<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/js/bootstrap.min.js"></script>
<script src="/js/bootstrap-paginator.js"></script>

<script>
    $(function () {
        var query = '<%= request.getAttribute("currentQuery") %>';
        $('#search-all').click(function () {
            window.location.href = '/result?query=' + query + '&mode=0&page=0';
        });
        $('#search-web').click(function () {
            window.location.href = '/result?query=' + query + '&mode=1&page=0';
        });
        $('#search-doc').click(function () {
            window.location.href = '/result?query=' + query + '&mode=2&page=0';
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

        $('#fuzzy-search-submmit').click(function () {
            $('#fuzzy-search-form').submit();
        });
        $('#wildcard-search-submmit').click(function () {
            $('#wildcard-search-form').submit();
        });
        $('#advanced-search-submmit').click(function () {
            $('#advanced-search-form').submit();
        });

        var options = {
            currentPage: <%= request.getAttribute("page") %> + 1,
            totalPages: <%= request.getAttribute("totalPage") %>,
            numberOfPages: 5,
            bootstrapMajorVersion: 3,
            tooltipTitles: function (type, page, current) {
                switch (type) {
                    case "first":
                        return "首页";
                    case "prev":
                        return "上一页";
                    case "next":
                        return "下一页";
                    case "last":
                        return "尾页";
                    case "page":
                        return "第" + page + "页";
                }
            },
            shouldShowPage: function (type, page, current) {
                return true;
            },
            onPageChanged: function (event, oldPage, newPage) {
            },
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "disabled" : "pointer-cursor";
            },
        };
        var page = $('#manager_pagination');
        page.bootstrapPaginator(options);
        page.bootstrapPaginator({
            onPageChanged: function (event, oldPage, newPage) {
                var h = window.location.href;
                if(h.indexOf("page=") != -1) {
                    window.location.href = h.replace("page=" + (oldPage-1), "page=" + (newPage-1));
                }
                else {
                    window.location.href = h + "&page=" + (newPage-1);
                }
            }
        });

    })
</script>
</body>
</html>
