import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

import { Footer } from './footer';

describe('CopyRight', () => {
  it('should render component', () => {
    const { getByTestId } = render(<Footer />);

    expect(getByTestId('instruction')).toHaveTextContent('Double-click to edit a todo');
  });
});
