package roman.com.booklisterapp.dataobjects;


/**
 * a dataobject class to hold the data returned from the books api
 */
public class Book {
    private String mTitle;
    private String mAuthor;
    private String mPublishedDate;
    private String mDescription;

    public Book(String title, String author, String publishedDate, String description) {
        mTitle = title;
        mAuthor = author;
        mPublishedDate = publishedDate;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getDescription() {
        return mDescription;
    }
}
