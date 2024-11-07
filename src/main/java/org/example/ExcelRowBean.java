package org.example;

public class ExcelRowBean {
    String pageType;
    String componentName;
    String assetFolderPath;
    int expectedWidth;
    int expectedHeight;

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setAssetFolderPath(String assetFolderPath) {
        this.assetFolderPath = assetFolderPath;
    }

    public void setExpectedWidth(int expectedWidth) {
        this.expectedWidth = expectedWidth;
    }

    public void setExpectedHeight(int expectedHeight) {
        this.expectedHeight = expectedHeight;
    }

    public String getPageType() {
        return pageType;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getAssetFolderPath() {
        return assetFolderPath;
    }

    public int getExpectedWidth() {
        return expectedWidth;
    }

    public int getExpectedHeight() {
        return expectedHeight;
    }
}
