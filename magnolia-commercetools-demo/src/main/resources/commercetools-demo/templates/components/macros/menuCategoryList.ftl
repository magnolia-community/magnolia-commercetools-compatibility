[#macro menuCategoryList categories siteRootLink categoriesDepth=-1 categoryId="root"]
    [#assign childCategories = ctfn.getChildCategoriesFromList(categories, categoryId)]
    [#if childCategories?has_content]
        [#if categoryId!="root"]<ul [#if categoriesDepth?number=-2]class="sub_menu"[/#if]>[/#if]
            [#if categoryId="root"]<li><a href="${siteRootLink}">Home</a></li>[/#if]
            [#list childCategories as childCategory]
                <li><a href="${siteRootLink}?categoryId=${childCategory.getId()}">${childCategory.getName().get(ctfn.getLanguage())}</a>
                    [#if categoriesDepth?number<0 || categoriesDepth?number-1>0]
                        [@menuCategoryList categories siteRootLink categoriesDepth?number-1 childCategory.getId() /]
                    [/#if]
                </li>
            [/#list]
        [#if categoryId!="root"]</ul>[/#if]
    [/#if]
[/#macro]