package top.mcwebsite.common.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.functions

open class BaseFragment<VB : ViewBinding> : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    private lateinit var _binding: VB

    protected val binding: VB get() = _binding!!

    open fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB? {
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bindingInternal = getViewBinding(inflater, container)
        if (bindingInternal == null) {
            // 反射有性能问题
            val superTypes = this::class.supertypes
            var aClass: KClass<VB>? = null
            for (type in superTypes) {
                if (type.classifier == BaseFragment::class) {
                    aClass = type.arguments.first().type?.classifier as KClass<VB>?
                }
            }
            val inflateMethod = aClass?.declaredFunctions?.find {
                it.name == "inflate" && it.parameters.size >= 2 && it.parameters.first().type.classifier == LayoutInflater::class &&
                        it.parameters[1].type.classifier == ViewGroup::class && it.parameters[2].type.classifier == Boolean::class
            }
            _binding = inflateMethod?.call(layoutInflater, container, false) as VB
            return _binding.root
        } else {
            _binding = bindingInternal
            return _binding.root
        }
    }

}