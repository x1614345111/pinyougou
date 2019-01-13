<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门小DEMO </title>
</head>
<body>
<!--html注释-->
<#--我只是一个注释，我不会有任何输出  -->
${name},你好。${message}

<#assign successMan="徐德昌"><br/>
成功的男人：${successMan}


<#if flag==true>
    true执行
<#else >
    false执行
</#if>
<br>
<#list goodsList as list >
   ${list_index} name:${list.name} price:${list.price}<br>
</#list>
</body>
<#include "head.ftl">
</html>
