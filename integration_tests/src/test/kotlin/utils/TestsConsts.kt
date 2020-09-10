package utils

const val defaultAndroidOutputPattern =   "AndroidArgs.*?" +
    "gcloud:.*?" +
    "flank:.*?" +
    "RunTests.*?" +
    "Matrices webLink.*?" +
    "matrix-.*?" +
    "FetchArtifacts.*?" +
    "Updating matrix file.*?" +
    "CostReport.*?MatrixResultsReport.*?" +
    "1 test cases passed, 1 skipped.*?" +
    "Uploading JUnitReport.xml ."

const val defaultIosOutputPattern = "IosArgs.*?" +
    "gcloud:.*?" +
    "flank:.*?" +
    "RunTests.*?" +
    "Matrices webLink.*?" +
    "matrix-.*?" +
    "FetchArtifacts.*?" +
    "Updating matrix file.*?" +
    "CostReport.*?" +
    "MatrixResultsReport.*?" +
    "test cases passed.*?" +
    "Uploading JUnitReport.xml ."
