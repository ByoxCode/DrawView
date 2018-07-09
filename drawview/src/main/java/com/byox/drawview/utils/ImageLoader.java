package com.byox.drawview.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.byox.drawview.enums.ImageType;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.List;

public class ImageLoader {
    /**
     * Load image into ImageView using Picasso library
     *
     * @param context               Application context
     * @param imageView             ImageView that contain the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     */
    public static void LoadImage(Context context, ImageView imageView, ImageType imageType,
                                 Object imageObj, List<Transformation> transformationList) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            requestCreator
                    .fit().centerCrop();

            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            requestCreator.into(imageView);
        }
    }

    /**
     * Load image into ImageView using Picasso library including scale type
     *
     * @param context               Application context
     * @param imageView             ImageView that contain the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param scaleType             image scale of type {@link android.widget.ImageView.ScaleType}
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     */
    public static void LoadImage(Context context, ImageView imageView, ImageType imageType,
                                 ImageView.ScaleType scaleType, Object imageObj,
                                 List<Transformation> transformationList) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            requestCreator
                    .fit();

            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestCreator.centerCrop();
            } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
                requestCreator.centerInside();
            }

            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            requestCreator.into(imageView);
        }
    }

    /**
     * Load image into ImageView using Picasso library including scale type and callback
     *
     * @param context               Application context
     * @param imageView             ImageView that contain the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param scaleType             image scale of type {@link android.widget.ImageView.ScaleType}
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     * @param callback              Picasso callback of type {@link com.squareup.picasso.Callback}
     */
    public static void LoadImage(Context context, ImageView imageView, ImageType imageType,
                                 ImageView.ScaleType scaleType, Object imageObj,
                                 List<Transformation> transformationList, Callback callback) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            requestCreator
                    .fit();

            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestCreator.centerCrop();
            } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
                requestCreator.centerInside();
            }

            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            if (callback == null) {
                requestCreator.into(imageView);
            } else {
                requestCreator.into(imageView, callback);
            }
        }
    }

    /**
     * Load image into ImageView using Picasso library including callback
     *
     * @param context               Application context
     * @param imageView             ImageView that contain the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     * @param callback              Picasso callback of type {@link com.squareup.picasso.Callback}
     */
    public static void LoadImage(Context context, ImageView imageView, ImageType imageType,
                                 Object imageObj, List<Transformation> transformationList,
                                 Callback callback) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            requestCreator
                    .fit().centerCrop();

            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            if (callback == null) {
                requestCreator.into(imageView);
            } else {
                requestCreator.into(imageView, callback);
            }
        }
    }

    /**
     * Load image into ImageView using Picasso library
     *
     * @param context               Application context
     * @param target                {@link Target} to load the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     */
    public static void LoadImage(Context context, Target target, ImageType imageType,
                                 Object imageObj, List<Transformation> transformationList) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            requestCreator.into(target);
        }
    }

    /**
     * Load image into ImageView using Picasso library
     *
     * @param context               Application context
     * @param target                {@link Target} to load the image
     * @param imageType             {@link com.byox.drawview.enums.ImageType} type
     * @param imageObj              image object of type {@link com.byox.drawview.enums.ImageType}
     * @param transformationList    list of transformations of type {@link com.squareup.picasso.Transformation}
     * @param callback              Picasso callback of type {@link com.squareup.picasso.Callback}
     */
    public static void LoadImage(Context context, Target target, ImageType imageType,
                                 Object imageObj, List<Transformation> transformationList,
                                 Callback callback) {
        RequestCreator requestCreator = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        switch (imageType) {
            case RESOURCE:
                if (imageObj instanceof Integer) {
                    requestCreator = builder.build()
                            .load((int) imageObj);
                } else if (imageObj instanceof String){
                    int resourceId = context.getResources()
                            .getIdentifier(String.valueOf(imageObj), "drawable",
                                    context.getPackageName());
                    requestCreator = builder.build().load(resourceId);
                }
                break;
            case URL:
                requestCreator = builder.build()
                        .load(String.valueOf(imageObj));
                break;
            case FILE:
                requestCreator = builder.build()
                        .load((File) imageObj);
                break;
            case ASSET:
                requestCreator = builder.build()
                        .load("file:///android_asset/" + String.valueOf(imageObj));
                break;
        }

        if (requestCreator != null) {
            if (transformationList != null) {
                requestCreator.transform(transformationList);
            }

            if (callback != null){
                requestCreator.fetch(callback);
            }

            requestCreator.into(target);
        }
    }
}
