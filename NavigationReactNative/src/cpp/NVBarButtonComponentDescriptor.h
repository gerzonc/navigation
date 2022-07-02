#pragma once

#include "NVBarButtonShadowNode.h"
#include <react/renderer/core/ConcreteComponentDescriptor.h>

namespace facebook {
namespace react {

/*
 * Descriptor for <Slider> component.
 */
class NVBarButtonComponentDescriptor final
    : public ConcreteComponentDescriptor<NVBarButtonShadowNode> {
 public:
    NVBarButtonComponentDescriptor(ComponentDescriptorParameters const &parameters)
      : ConcreteComponentDescriptor(parameters),
        imageManager_(std::make_shared<ImageManager>(contextContainer_)){}

  void adopt(ShadowNode::Unshared const &shadowNode) const override {
    ConcreteComponentDescriptor::adopt(shadowNode);

    auto NVBarButtonShadowNode =
        std::static_pointer_cast<NVBarButtonShadowNode>(shadowNode);

    // `SliderShadowNode` uses `ImageManager` to initiate image loading and
    // communicate the loading state and results to mounting layer.
    NVBarButtonShadowNode->setImageManager(imageManager_);
  }

 private:
  const SharedImageManager imageManager_;
};

} // namespace react
} // namespace facebook