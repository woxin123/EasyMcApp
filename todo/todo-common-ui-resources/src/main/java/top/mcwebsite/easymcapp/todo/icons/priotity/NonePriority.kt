package top.mcwebsite.easymcapp.todo.icons.priotity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import top.mcwebsite.easymcapp.todo.icons.ToDoIcons

public val ToDoIcons.NonePriority: ImageVector
    get() {
        if (_nonePriority != null) {
            return _nonePriority!!
        }
        _nonePriority = Builder(name = "NonePriority", defaultWidth = 200.0.dp, defaultHeight =
        200.0.dp, viewportWidth = 1024.0f, viewportHeight = 1024.0f).apply {
            path(fill = SolidColor(Color(0xFF333333)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero) {
                moveTo(198.4f, 870.4f)
                curveTo(171.52f, 870.4f, 153.6f, 854.37f, 153.6f, 830.31f)
                verticalLineTo(221.29f)
                curveToRelative(0.0f, -20.02f, 13.47f, -40.04f, 31.33f, -48.08f)
                curveToRelative(17.92f, -7.99f, 44.8f, -7.99f, 62.77f, 0.0f)
                curveToRelative(49.25f, 28.06f, 143.36f, 64.1f, 241.87f, 20.02f)
                curveToRelative(156.83f, -68.1f, 286.72f, -32.05f, 336.03f, -11.98f)
                curveToRelative(26.88f, 11.98f, 44.8f, 36.04f, 44.8f, 64.1f)
                verticalLineToRelative(320.51f)
                curveToRelative(0.0f, 24.06f, -13.41f, 48.13f, -35.84f, 64.15f)
                curveToRelative(-22.43f, 16.03f, -49.31f, 19.97f, -80.64f, 11.98f)
                curveToRelative(-49.31f, -11.98f, -129.89f, -20.02f, -224.0f, 20.07f)
                curveToRelative(-116.48f, 52.07f, -219.49f, 24.06f, -286.72f, -4.04f)
                verticalLineToRelative(172.29f)
                curveToRelative(0.0f, 24.06f, -17.92f, 40.09f, -44.8f, 40.09f)
                close()
            }
        }
        .build()
        return _nonePriority!!
    }

private var _nonePriority: ImageVector? = null
