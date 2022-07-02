#include "NVBarButtonShadowNode.h"
#include <react/renderer/core/LayoutContext.h>

namespace facebook {
namespace react {

extern const char NVBarButtonComponentName[] = "NVBarButton";

void NVBarButtonShadowNode::setImageManager(const SharedImageManager &imageManager) {
  ensureUnsealed();
  imageManager_ = imageManager;
}

void NVBarButtonShadowNode::updateStateIfNeeded() {
  const auto &newImageSource = getImageSource();

  auto const &currentState = getStateData();

  auto imageSource = currentState.getImageSource();

  bool anyChanged = newImageSource != imageSource;

  if (!anyChanged) {
    return;
  }

  // Now we are about to mutate the Shadow Node.
  ensureUnsealed();

  // It is not possible to copy or move image requests from SliderLocalData,
  // so instead we recreate any image requests (that may already be in-flight?)
  // TODO: check if multiple requests are cached or if it's a net loss
  auto state = NVBarButtonState{
      newImageSource,
      imageManager_->requestImage(newImageSource, getSurfaceId()),
    };
  setStateData(std::move(state));
}

ImageSource NVBarButtonShadowNode::getImageSource() const {
  return getConcreteProps().image;
}

#pragma mark - LayoutableShadowNode

Size NVBarButtonShadowNode::measureContent(
    LayoutContext const &layoutContext,
    LayoutConstraints const &layoutConstraints) const {
  return {};
}

void NVBarButtonShadowNode::layout(LayoutContext layoutContext) {
  updateStateIfNeeded();
  ConcreteViewShadowNode::layout(layoutContext);
}

} // namespace react
} // namespace facebook