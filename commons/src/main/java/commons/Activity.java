package commons;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * The Activity Entity
 * We use this class to store the activities in the database
 */
@Entity(name = "Activity")
public class Activity {

    private String id;
    private String image_path;
    private String title;
    private Long consumption_in_wh;
    private String source;

    /**
     * Instantiates a new Activity.
     *
     * @param id                the id (the activity's id)
     * @param image_path        the image path (image for the activity)
     * @param title             the title (description of the activity)
     * @param consumption_in_wh the consumption in wh (average energy consumption for the activity)
     * @param source            the source (URL from where the consumption value was taken)
     */
    public Activity(String id, String image_path, String title, Long consumption_in_wh, String source) {
        this.id = id;
        this.image_path = image_path;
        this.title = title;
        this.consumption_in_wh = consumption_in_wh;
        this.source = source;
    }

    /**
     * Instantiates a new Activity
     * The empty, package level constructor is used for object mappers.
     */
    Activity() {

    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets image path.
     *
     * @return the image path
     */
    public String getImage_path() {
        return image_path;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets consumption in wh.
     *
     * @return the consumption in wh
     */
    public Long getConsumption_in_wh() {
        return consumption_in_wh;
    }

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id) && Objects.equals(image_path, activity.image_path) && Objects.equals(title, activity.title) && Objects.equals(consumption_in_wh, activity.consumption_in_wh) && Objects.equals(source, activity.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image_path, title, consumption_in_wh, source);
    }

}
