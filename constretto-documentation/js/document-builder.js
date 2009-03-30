(function($) {


    $(document).ready(function() {
        var toc = $('.toc');
        toc.append('<p class="title">Table of Contents</p>');
        toc.append('<ul id="toc_ul"></ul>');
        var toc_ul = toc.find('#toc_ul');
        $('.section').each(function() {
            createSectionTiles(jQuery(this), toc_ul);
        });
        sh_highlightDocument();
    });

    function createSectionTiles(section, toc) {
        var title = section.attr('title');
        var id = section.attr('id');
        section.prepend("<h1>" + title + "</h1>");
        toc.append('<li class="section"><a href="#' + id + '">' + title + '</a></li>');
        section.find('.subsection').each(function() {
            createSubSectionTitles(jQuery(this), toc);
        });
    }

    function createSubSectionTitles(section, toc) {
        var title = section.attr('title');
        var id = section.attr('id');
        toc.append('<li class="subsection"><a href="#' + id + '">' + title + '</a></li>');
        section.prepend("<h2>" + title + "</h2>");
    }

})(jQuery);

