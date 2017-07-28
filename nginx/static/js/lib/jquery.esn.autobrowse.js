/**
 * Version 2.0
 *
 * Written by Micael Sjölund, ESN (http://www.esn.me)
 *
 * Creates a growing container that automatically fills its content via ajax requests, when the user scrolls to the
 * bottom of the container. More info: http://pushingtheweb.com/2010/09/endless-scroller-jquery-plugin/
 *
 * Requires jStorage (), if the useCache option is set to true. WARNING: Somewhat experimental. See below for more info.
 *
 *
 * Usage:
 * .autobrowse(options)
 *    options   Map of property-value options which controls plugin behaviour.
 * .autobrowse(command)
 *    command   String command that can be sent to the plugin.
 *
 *
 * * COMMANDS
 * * "flush"        Clears the plugin cache
 *
 *
 * * OPTIONS
 * * url            Callback to render url from offset argument.
 *                  Example: function (offset) { return "http://mydomain.com/OFFSET/20".replace(/OFFSET/, offset) }
 * * template       Callback to render markup from json response.
 *                  Example: function (response) { var markup=''; for (var i=0; i<response.length; i++) { markup+='<img src="'+response[i]+'" />' } return markup; }
 * * itemsReturned  Callback that is run on ajax json response to determine how many items was returned
 *
 * * OPTIONAL OPTIONS
 * * loader         Element, jQuery object or markup to indicate to the user that the site is waiting for more items.
 * * offset         Item offset for first ajax call to url, if you have already rendered items on the page
 * * max            Maximum number of items to be retrieved by the plugin (can also be used to tell the plugin how many
 *                  items there are in total on the server, so that no unneccesary requests are made.
 * * complete       Callback that is run when the element has been updated with new content. This is run before the
 *                  response is stored (if using useCache), making it is possible to manipulate the response here before
 *                  it is stored.
 * * sensitivity    Number of pixels before end of element that the plugin will start fetching more items.
 * * postData       If you want to do a POST request instead of a GET request, supply this argument, either as a
 *                  function or an object. If not set, a GET request will be made.
 * * useCache       If true, the plugin will use browser storage to keep the state between page loads. If the user
 *                  clicks away from the page and then goes back, all items fetched will be rendered again, and the
 *                  user will see the same view as when he left the page. Requires http://www.jstorage.info/.
 *                  WARNING: This doesn't work with original jStorage. A modified version is
 *                  available on http://github.com/msjolund/jquery-esn-autobrowse. jStorage also
 *                  requires jquery-json: http://code.google.com/p/jquery-json/. Default: false
 * * expiration     How long to keep cache, in hours. Default: 24
 * * stopFunction 	a function that will return true if it is necessary to stop autoscrolling
 * * onError		a function that will be executed on error (HTTP response 500, etc.)
 *
 */
(function( $ ){
jQuery.fn.autobrowse = function (options)
{
    var defaults = {
        url: function (offset) { return "/"; },
        template: function (response) { return ""; },
        offset: 0,
        max: null,
        loader: '<div class="loader"></div>',
        itemsReturned: function (response) { return response.length },
        complete: function (response) {},
        finished: function (response) {},
        useCache: false,
        expiration: 24,
        sensitivity: 0,
        postData: null,
		stopFunction: function () {},
		onError: function () {},
        total:undefined
    };

    // flush cache command
    if (typeof options == "string" && options == "flush")
    {
        jQuery.jStorage.flush();
        return this;
    }

    options = jQuery.extend(defaults, options);
    // allow non-dynamic url
    if (typeof options.url == "string")
    {
        var url = options.url;
        options.url = function (offset) { return url; }
    }

    var getDataLength = function (data)
    {
        var length = 0;
        for (var i = 0; i < data.length; i++)
            length += options.itemsReturned(data[i]);
        return length;
    };

    return this.each( function ()
    {
        var localData, obj = jQuery(this);
        var currentOffset = options.offset;
        var loading = false;
        var scrollTopUpdateTimer = null;
		var stopping = false;

        var _stopPlugin = function (handler)
        {
            jQuery(options.content).unbind("scroll", handler);
            options.finished.call(obj);
        };

        var scrollCallback = function (content)
        {
            content = options.content;
            obj = $(options.objName);
            var scrollTop = jQuery(content).scrollTop();
            // var objBottom = obj.height() + obj.offset().top;
            var objBottom = obj.height();
            var winHeight = content.innerHeight() ? content.innerHeight() : $(content).height();
            var winBtmPos = scrollTop + winHeight;
            if (scrollTopUpdateTimer)
                clearTimeout(scrollTopUpdateTimer);
			if (options.useCache) {
				scrollTopUpdateTimer = setTimeout(function () { jQuery.jStorage.set("autobrowseScrollTop", scrollTop); }, 200);
			}
            if (objBottom  < winBtmPos + options.sensitivity && !loading)
            {
                var loader = jQuery(options.loader);
                loader.appendTo(obj);
                loading = true;
                var ajaxCallback = function (response,data) {
                    options.initPage(response);
                    options.offset = response.data.currPage + 1;
                    options.total = response.data.totalPage;
                    if (options.itemsReturned(response) > 0)
                    {
                        // Create the markup and append it to the container
                        try { var markup = options.template(response); }
                        catch (e) { console.error(e) } // ignore for now
                        var newElements = jQuery(markup);
                        newElements.appendTo(obj);

                        // Call user onComplete callback
                        options.complete.call(obj, response.data.result, newElements);

                        // Store in local cache if option is set, and everything fetched fitted into the storage
                        if (options.useCache && getDataLength(localData) + options.offset == currentOffset)
                        {
                            localData.push(response.data.result);
                            if (!jQuery.jStorage.set("autobrowseStorage", localData))
                                // Storage failed, remove last pushed response
                                localData.pop();
                        }

                        // Update offsets
                        currentOffset += options.itemsReturned(response);
                        if (options.useCache)
                        {
                            jQuery.jStorage.set("autobrowseOffset", currentOffset);
                        }
                    }

                    loader.remove();
                    // Check if these were the last items to fetch from the server, if so, stop listening
                  if (options.itemsReturned(response) == 0 || (options.max != null && currentOffset >= options.max) || options.stopFunction(response) === true)
                    {
                        _stopPlugin(scrollCallback)
                        stopping = true;
                    }
                    loading = false;

					if (!stopping) {
                        scrollCallback();
					}
                };
                var stop = jQuery.jStorage.get("stop");
                if (stop)
                {
                    return this;
                }
                if (options.postData)
                {

                    options.postData =  jQuery.jStorage.get("postData");
                    options.url = function () { return jQuery.jStorage.get("url");}
                    var data = null;
                    if (typeof options.postData == "function")
                    {
                        data = options.postData();
                    }
                    else
                    {
                        if(options.offset > 0){
                            options.postData.data.currPage = options.offset;
                        }
                        data = options.postData;
                    }
                    if(options.postData.data.currPage > options.total){
                        return this;
                    }
                    // jQuery.post(options.url(currentOffset),data, ajaxCallback, "json").error(options.onError);
                    $.ajax({
                        url: options.url(currentOffset),
                        type: "post",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify(data),
                        async: false,
                        success: function (res) {
                            ajaxCallback(res,data);
                        }
                    });
                }
                else
                {
                    jQuery.getJSON(options.url(currentOffset), ajaxCallback).error(options.onError);
                }
            }
        };

        var _startPlugin = function()
        {
            if (options.useCache)
                var autobrowseScrollTop = jQuery.jStorage.get("autobrowseScrollTop");
            if (autobrowseScrollTop)
                jQuery(options.content).scrollTop(autobrowseScrollTop);
            jQuery(options.content).scroll(scrollCallback);
            scrollCallback();
        };


        if (options.useCache)
        {
            if (jQuery.jStorage.get("autobrowseStorageKey") != options.url(0,0))
            {
                // flush cache if wrong page
                jQuery.jStorage.flush();
            }
            else if (jQuery.jStorage.get("autobrowseExpiration") && jQuery.jStorage.get("autobrowseExpiration") < (new Date()).getTime())
            {
                // flush cache if it's expired
                jQuery.jStorage.flush();
            }
            localData= jQuery.jStorage.get("autobrowseStorage");
            if (localData)
            {
                // for each stored ajax response
                for (var i = 0; i < localData.length; i++)
                {
                    var markup = options.template(localData[i]);
                    jQuery(markup).appendTo(obj);
                    currentOffset += options.itemsReturned(localData[i]);
                    options.complete.call(obj, localData[i]);
                }
                _startPlugin();
            }
            else
            {
                localData = [];
                jQuery.jStorage.get("autobrowseStorageKey")
                jQuery.jStorage.set("autobrowseExpiration", (new Date()).getTime()+options.expiration*60*60*1000);
                jQuery.jStorage.set("autobrowseOffset", currentOffset);
                jQuery.jStorage.set("autobrowseStorageKey", options.url(0, 0));
                jQuery.jStorage.set("autobrowseStorage", localData);
                jQuery.jStorage.set("autobrowseScrollTop", 0);
                _startPlugin();
            }
        }

        else
        {
            _startPlugin();
        }
    });
};
})( jQuery );
